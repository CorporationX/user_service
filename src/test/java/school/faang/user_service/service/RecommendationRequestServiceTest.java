package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.filter.recommendation.RecommendationRequestMessageFilter;
import school.faang.user_service.filter.recommendation.RecommendationRequestStatusFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = RecommendationRequestMapper.INSTANCE;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    void getRequestThrowsException() {
        when(recommendationRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
                () -> recommendationRequestService.getRequest(anyLong()));

        assertEquals("There is no recommendation with such id", illegalStateException.getMessage());
    }

    @Test
    void getRequest() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1);

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));
        RecommendationRequestDto recommendationRequestWithValue = recommendationRequestService.getRequest(1);

        assertNotNull(recommendationRequestWithValue);
        assertEquals(1, recommendationRequestWithValue.getId());
    }

    @Test
    void listToListTest() {
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setId(1);

        List<SkillRequest> skills = List.of(skillRequest);

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setSkills(skills);
        recommendationRequest.setId(1);
        recommendationRequest.setMessage("msg");

        RecommendationRequestDto recommendationRequestDto = recommendationRequestMapper.toDto(recommendationRequest);

        assertNotNull(recommendationRequestDto);
        assertEquals(1, recommendationRequestDto.getId());
        assertEquals("msg", recommendationRequestDto.getMessage());
        assertEquals(1, recommendationRequestDto.getSkillsId().get(0));
    }

    @Test
    void rejectRequestValidData() {
        long requestId = 1;
        String rejectionReason = "Not suitable for the position";

        RejectionDto rejectionDto = new RejectionDto(rejectionReason);

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(requestId);
        recommendationRequest.setStatus(RequestStatus.PENDING);

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(recommendationRequest));
        RecommendationRequestDto result = recommendationRequestService.rejectRequest(requestId, rejectionDto);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals(RequestStatus.REJECTED, recommendationRequest.getStatus());
        assertEquals(rejectionReason, recommendationRequest.getRejectionReason());
        verify(recommendationRequestRepository).findById(requestId);
    }

    @Test
    void rejectRequestInvalidRequest() {
        long requestId = 1;
        String rejectionReason = "Not suitable for the position";

        RejectionDto rejectionDto = new RejectionDto(rejectionReason);

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(requestId, rejectionDto));

        assertEquals("Recommendation with id: " + requestId + " does not exist", exception.getMessage());
        verify(recommendationRequestRepository).findById(requestId);
    }

    @Test
    void testRecommendationRequestMessageFilter() {
        List<RecommendationRequestFilter> requestFilters = List.of(new RecommendationRequestMessageFilter());

        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setMessage("Hello");
        recommendationRequest2.setMessage("Goodbye");
        recommendationRequest3.setMessage("Hel");

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RecommendationRequestFilterDto recommendationRequestFilterDto =
                RecommendationRequestFilterDto.builder()
                        .messagePattern("Hel")
                        .build();

        when(recommendationRequestRepository.findAll()).thenReturn(requests);

        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, recommendationRequestMapper, requestFilters, userRepository, skillRequestRepository);

        List<RecommendationRequestDto> eventsByFilter = recommendationRequestService.getRecommendationRequests(recommendationRequestFilterDto);
        assertEquals(2, eventsByFilter.size());
        assertEquals("Hello", eventsByFilter.get(0).getMessage());
        assertEquals("Hel", eventsByFilter.get(1).getMessage());
    }

    @Test
    void testRecommendationRequestStatusFilter() {
        List<RecommendationRequestFilter> requestFilters = List.of(new RecommendationRequestStatusFilter());

        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setStatus(RequestStatus.ACCEPTED);
        recommendationRequest2.setStatus(RequestStatus.PENDING);
        recommendationRequest3.setStatus(RequestStatus.PENDING);

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RecommendationRequestFilterDto recommendationRequestFilterDto =
                RecommendationRequestFilterDto.builder()
                        .status(RequestStatus.PENDING)
                        .build();

        when(recommendationRequestRepository.findAll()).thenReturn(requests);

        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, recommendationRequestMapper, requestFilters, userRepository, skillRequestRepository);

        List<RecommendationRequestDto> eventsByFilter = recommendationRequestService.getRecommendationRequests(recommendationRequestFilterDto);

        assertEquals(2, eventsByFilter.size());
        assertEquals(RequestStatus.PENDING, eventsByFilter.get(0).getStatus());
        assertEquals(RequestStatus.PENDING, eventsByFilter.get(1).getStatus());
    }


    @Test
    void testRecommendationRequestMessageAndStatusFilter() {
        List<RecommendationRequestFilter> requestFilters = List.of(new RecommendationRequestMessageFilter(), new RecommendationRequestStatusFilter());

        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setMessage("Hello");
        recommendationRequest1.setStatus(RequestStatus.ACCEPTED);

        recommendationRequest2.setMessage("Goodbye");
        recommendationRequest2.setStatus(RequestStatus.PENDING);

        recommendationRequest3.setMessage("Hel");
        recommendationRequest3.setStatus(RequestStatus.ACCEPTED);

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RecommendationRequestFilterDto recommendationRequestFilterDto =
                RecommendationRequestFilterDto.builder()
                        .messagePattern("Hel")
                        .status(RequestStatus.ACCEPTED)
                        .build();

        when(recommendationRequestRepository.findAll()).thenReturn(requests);

        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, recommendationRequestMapper,requestFilters,userRepository,skillRequestRepository);

        List<RecommendationRequestDto> eventsByFilter = recommendationRequestService.getRecommendationRequests(recommendationRequestFilterDto);

        assertEquals(2, eventsByFilter.size());
        assertEquals("Hello", eventsByFilter.get(0).getMessage());
        assertEquals(RequestStatus.ACCEPTED, eventsByFilter.get(0).getStatus());
    }
}