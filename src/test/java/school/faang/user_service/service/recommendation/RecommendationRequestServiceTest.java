package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.dto.recomendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recomendation.RecommendationRequestMapper;
import school.faang.user_service.mapper.recomendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.filters.RecommendationRequestFilter;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    private RecommendationRequestDto recommendationRequestDto;

    @Mock
    private List<RecommendationRequestFilter> recommendationRequestFilters;
    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;
    @Captor
    private ArgumentCaptor<RecommendationRequest> recommendationRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<List<RecommendationRequest>> recommendationRequestsCaptor;
    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = new RecommendationRequestMapperImpl();
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;


    @InjectMocks
    RecommendationRequestService recommendationRequestService;

    @Test
    public void testGetRequests() {
        List<RecommendationRequest> recommendationRequests = prepareRecommendationRequests();
        List<RecommendationRequestFilterDto> recommendationRequestFilterDtos = prepareRequestFilterDtos();
        List<RecommendationRequestDto> recommendationRequestDtos = recommendationRequestService.getRequests(recommendationRequestFilterDtos.get(0));

        when(recommendationRequestRepository.findAll().stream()).thenReturn(recommendationRequests.stream());
        //TODO need to think
//        when(recommendationRequestFilters.stream().filter().toList()).thenReturn(List.of(new RecommendationRequest(),new RecommendationRequest()).stream().toList());
        verify(recommendationRequestMapper, times(1)).mapToDto(recommendationRequestsCaptor.capture());
        assertAll(
                () -> assertEquals(1, recommendationRequestDtos.size()),
                () -> assertEquals(recommendationRequestDtos.get(0).getId(), recommendationRequests.get(0).getId()),
                () -> assertEquals(recommendationRequestDtos.get(0).getStatus(), recommendationRequests.get(0).getStatus()),
                () -> assertEquals(recommendationRequestDtos.get(0).getReceiverId(), recommendationRequests.get(0).getReceiver().getId()),
                () -> assertEquals(recommendationRequestDtos.get(0).getRequesterId(), recommendationRequests.get(0).getRequester().getId())
        );
    }

    List<RecommendationRequestFilterDto> prepareRequestFilterDtos() {
        List<RecommendationRequestFilterDto> recommendationRequestFilterDtos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            recommendationRequestFilterDtos.add(new RecommendationRequestFilterDto());
        }
        recommendationRequestFilterDtos.get(0).setMessagePattern("someMessage");
        recommendationRequestFilterDtos.get(1).setRequestIdPattern(10L);
        recommendationRequestFilterDtos.get(2).setReceiverIdPattern(20L);
        recommendationRequestFilterDtos.get(3).setStatusPattern(RequestStatus.ACCEPTED);
        recommendationRequestFilterDtos.get(4).setStatusPattern(RequestStatus.PENDING);
        recommendationRequestFilterDtos.get(4).setMessagePattern("testMessage");
        return recommendationRequestFilterDtos;
    }

    List<RecommendationRequest> prepareRecommendationRequests() {
        List<RecommendationRequest> recommendationRequests = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            recommendationRequests.add(new RecommendationRequest());
        }

        recommendationRequests.get(0).setMessage("someMessage");
        recommendationRequests.get(1).setRequester(new User() {{
            setId(10L);
        }});
        recommendationRequests.get(2).setReceiver(new User() {{
            setId(20L);
        }});
        recommendationRequests.get(3).setStatus(RequestStatus.ACCEPTED);
        recommendationRequests.get(4).setMessage("testMessage");
        recommendationRequests.get(4).setStatus(RequestStatus.PENDING);
        return recommendationRequests;
    }


    @Test
    public void testCreateSaveDb() {
        recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setRequesterId(5L);
        recommendationRequestDto.setReceiverId(1L);
        recommendationRequestDto.setSkillsIds(List.of(1L, 2L));
        recommendationRequestDto.setId(2L);
        User requesterUser = new User();
        requesterUser.setId(recommendationRequestDto.getRequesterId());
        User receiverUser = new User();
        receiverUser.setId(recommendationRequestDto.getReceiverId());
        Skill firstSkill = new Skill();
        firstSkill.setId(0);
        Skill secondSkill = new Skill();
        secondSkill.setId(1);
        SkillRequest firstSkillRequest = new SkillRequest();
        firstSkillRequest.setId(recommendationRequestDto.getSkillsIds().get(0));
        firstSkillRequest.setSkill(firstSkill);
        SkillRequest secondSkillRequest = new SkillRequest();
        secondSkillRequest.setId(recommendationRequestDto.getSkillsIds().get(1));
        secondSkillRequest.setSkill(secondSkill);

        when(skillRequestRepository.findAllById(recommendationRequestDto.getSkillsIds())).thenReturn(List.of(firstSkillRequest, secondSkillRequest));
        recommendationRequestService.create(recommendationRequestDto);

        verify(skillRequestRepository, times(1)).create(firstSkillRequest.getId(), firstSkillRequest.getSkill().getId());
        verify(skillRequestRepository, times(1)).create(secondSkillRequest.getId(), secondSkillRequest.getSkill().getId());
        verify(recommendationRequestRepository, times(1)).save(recommendationRequestArgumentCaptor.capture());

        RecommendationRequest recommendationRequestEntity = recommendationRequestArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals(recommendationRequestEntity.getId(), recommendationRequestDto.getId()),
                () -> assertEquals(recommendationRequestEntity.getMessage(), recommendationRequestDto.getMessage()),
                () -> assertEquals(recommendationRequestEntity.getCreatedAt(), recommendationRequestDto.getCreatedAt()),
                () -> assertEquals(recommendationRequestEntity.getReceiver().getId(), recommendationRequestDto.getReceiverId()),
                () -> assertEquals(recommendationRequestEntity.getRequester().getId(), recommendationRequestDto.getRequesterId()),
                () -> assertEquals(recommendationRequestEntity.getStatus(), recommendationRequestDto.getStatus()),
                () -> assertEquals(recommendationRequestEntity.getStatus(), recommendationRequestDto.getStatus()),
                () -> assertEquals(recommendationRequestEntity.getSkills().stream().map(SkillRequest::getId).toList(), recommendationRequestDto.getSkillsIds())
        );
    }
}
