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
import org.springframework.test.util.ReflectionTestUtils;
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
import school.faang.user_service.service.recommendation.filters.RecommendationRequestMessageFilter;
import school.faang.user_service.service.recommendation.filters.RecommendationRequestReceiverFilter;
import school.faang.user_service.service.recommendation.filters.RecommendationRequestRequesterFilter;
import school.faang.user_service.service.recommendation.filters.RecommendationRequestStatusFilter;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    private RecommendationRequestDto recommendationRequestDto;
    @Spy
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
    public void testFindByIdSuccessful() {
        RecommendationRequest resultRecommendationRequest = new RecommendationRequest();
        resultRecommendationRequest.setSkills(createSkillRequests());
        resultRecommendationRequest.setId(1L);

        when(recommendationRequestRepository.findById(resultRecommendationRequest.getId())).thenReturn(Optional.of(resultRecommendationRequest));

        RecommendationRequestDto resultDto = recommendationRequestService.getRequest(resultRecommendationRequest.getId());
        assertEquals(resultDto.getId(), resultRecommendationRequest.getId());
    }

    @Test
    public void testFindByThrowException() {
        RecommendationRequest resultRecommendationRequest = new RecommendationRequest();
        resultRecommendationRequest.setSkills(createSkillRequests());
        resultRecommendationRequest.setId(1L);

        when(recommendationRequestRepository.findById(resultRecommendationRequest.getId()))
                .thenReturn(Optional.ofNullable(null));
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                recommendationRequestService.getRequest(resultRecommendationRequest.getId()));
        assertEquals("No such element in db", exception.getMessage());
    }


    @Test
    public void testGetRequestsMessageFilter() {
        List<RecommendationRequestFilter> recommendationRequestFilters = initializeRequestFilters();
        List<RecommendationRequest> recommendationRequestsPrepare = prepareRecommendationRequests();
        List<RecommendationRequestFilterDto> recommendationRequestFilterDtos = prepareRequestFilterDtos();

        ReflectionTestUtils.setField(recommendationRequestService, "recommendationRequestFilters", recommendationRequestFilters);

        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequestsPrepare);
        List<RecommendationRequestDto> recommendationRequestDtos = recommendationRequestService.getRequests(recommendationRequestFilterDtos.get(0));

        verify(recommendationRequestMapper, times(1)).mapToDto(recommendationRequestsCaptor.capture());
        assertAll(
                () -> assertEquals(1, recommendationRequestDtos.size()),
                () -> assertEquals(recommendationRequestDtos.get(0).getMessage(), recommendationRequestsPrepare.get(0).getMessage())
        );
    }

    @Test
    public void testGetRequestsStatusFilter() {
        List<RecommendationRequestFilter> recommendationRequestFilters = initializeRequestFilters();
        List<RecommendationRequest> recommendationRequestsPrepare = prepareRecommendationRequests();
        List<RecommendationRequestFilterDto> recommendationRequestFilterDtos = prepareRequestFilterDtos();

        ReflectionTestUtils.setField(recommendationRequestService, "recommendationRequestFilters", recommendationRequestFilters);

        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequestsPrepare);
        List<RecommendationRequestDto> recommendationRequestDtos = recommendationRequestService.getRequests(recommendationRequestFilterDtos.get(3));

        verify(recommendationRequestMapper, times(1)).mapToDto(recommendationRequestsCaptor.capture());
        assertAll(
                () -> assertEquals(1, recommendationRequestDtos.size()),
                () -> assertEquals(recommendationRequestDtos.get(0).getStatus(), recommendationRequestsPrepare.get(3).getStatus())
        );
    }

    @Test
    public void testGetRequestsRequesterIdFilter() {
        List<RecommendationRequestFilter> recommendationRequestFilters = initializeRequestFilters();
        List<RecommendationRequest> recommendationRequestsPrepare = prepareRecommendationRequests();
        List<RecommendationRequestFilterDto> recommendationRequestFilterDtos = prepareRequestFilterDtos();

        ReflectionTestUtils.setField(recommendationRequestService, "recommendationRequestFilters", recommendationRequestFilters);

        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequestsPrepare);
        List<RecommendationRequestDto> recommendationRequestDtos = recommendationRequestService.getRequests(recommendationRequestFilterDtos.get(3));

        verify(recommendationRequestMapper, times(1)).mapToDto(recommendationRequestsCaptor.capture());
        assertAll(
                () -> assertEquals(1, recommendationRequestDtos.size()),
                () -> assertEquals(recommendationRequestDtos.get(0).getRequesterId(), recommendationRequestsPrepare.get(2).getRequester().getId())
        );
    }

    List<RecommendationRequestFilter> initializeRequestFilters() {
        return List.of(
                new RecommendationRequestStatusFilter(),
                new RecommendationRequestReceiverFilter(),
                new RecommendationRequestRequesterFilter(),
                new RecommendationRequestMessageFilter()
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
        List<SkillRequest> skillRequests = createSkillRequests();
        List<User> users = createUsers();
        List<RecommendationRequest> recommendationRequests = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            recommendationRequests.add(new RecommendationRequest());
        }

        recommendationRequests.get(0).setMessage("someMessage");
        recommendationRequests.get(0).setRequester(users.get(0));
        recommendationRequests.get(0).setReceiver(users.get(1));
        recommendationRequests.get(0).setSkills(skillRequests);
        recommendationRequests.get(0).setStatus(RequestStatus.PENDING);

        recommendationRequests.get(1).setMessage("text");
        recommendationRequests.get(1).setRequester(users.get(2));
        recommendationRequests.get(1).setReceiver(users.get(3));
        recommendationRequests.get(1).setSkills(skillRequests);
        recommendationRequests.get(1).setStatus(RequestStatus.PENDING);

        recommendationRequests.get(2).setMessage("bla bla bla");
        recommendationRequests.get(2).setRequester(users.get(4));
        recommendationRequests.get(2).setReceiver(users.get(5));
        recommendationRequests.get(2).setSkills(skillRequests);
        recommendationRequests.get(2).setStatus(RequestStatus.PENDING);

        recommendationRequests.get(3).setMessage("bla bla bla");
        recommendationRequests.get(3).setRequester(users.get(4));
        recommendationRequests.get(3).setReceiver(users.get(5));
        recommendationRequests.get(3).setSkills(skillRequests);
        recommendationRequests.get(3).setStatus(RequestStatus.ACCEPTED);

        recommendationRequests.get(4).setMessage("testMessage");
        recommendationRequests.get(4).setRequester(users.get(6));
        recommendationRequests.get(4).setReceiver(users.get(7));
        recommendationRequests.get(4).setStatus(RequestStatus.PENDING);
        recommendationRequests.get(4).setSkills(skillRequests);
        return recommendationRequests;
    }

    List<SkillRequest> createSkillRequests() {
        List<SkillRequest> skillRequests = new ArrayList<>();
        SkillRequest firstSkillRequest = new SkillRequest();
        firstSkillRequest.setId(10L);
        firstSkillRequest.setSkill(new Skill());

        SkillRequest secondSkillRequest = new SkillRequest();
        secondSkillRequest.setId(20L);
        secondSkillRequest.setSkill(new Skill());
        skillRequests.add(firstSkillRequest);
        skillRequests.add(secondSkillRequest);

        return skillRequests;
    }

    List<User> createUsers() {
        List<User> users = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            long finalI = i;
            users.add(new User() {{
                setId(finalI);
            }});
        }
        return users;
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
