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

import java.util.List;
import java.util.stream.Stream;

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
    private RecommendationRequestValidator recommendationRequestValidator;
    @Captor
    private ArgumentCaptor<RecommendationRequest> recommendationRequestArgumentCaptor;
    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = new RecommendationRequestMapperImpl();
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    List<RecommendationRequestFilter> recommendationRequestFilters;
    @InjectMocks
    RecommendationRequestService recommendationRequestService;

    @Test
    public void testGetRequests() {
        when(recommendationRequestRepository.findAll().stream()).thenReturn(Stream.of(new RecommendationRequest(), new RecommendationRequest()));
        when(recommendationRequestFilters.stream().toList()).thenReturn(List.of())
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
