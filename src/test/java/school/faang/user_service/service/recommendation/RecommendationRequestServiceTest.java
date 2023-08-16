package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestCreatedAfterFilter;
import school.faang.user_service.filter.recommendation.RecommendationRequestCreatedBeforeFilter;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.filter.recommendation.RecommendationRequestMessageFilter;
import school.faang.user_service.filter.recommendation.RecommendationRequestStatusFilter;
import school.faang.user_service.filter.recommendation.RecommendationRequestUpdatedAfterFilter;
import school.faang.user_service.filter.recommendation.RecommendationRequestUpdatedBeforeFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = RecommendationRequestMapper.INSTANCE;

    private RecommendationRequestService recommendationRequestService;

    @BeforeEach
    void startup() {
        RecommendationRequestUpdatedBeforeFilter recommendationRequestUpdatedBeforeFilter = new RecommendationRequestUpdatedBeforeFilter();
        RecommendationRequestUpdatedAfterFilter recommendationRequestUpdatedAfterFilter = new RecommendationRequestUpdatedAfterFilter();
        RecommendationRequestCreatedBeforeFilter recommendationRequestCreatedBeforeFilter = new RecommendationRequestCreatedBeforeFilter();
        RecommendationRequestCreatedAfterFilter recommendationRequestCreatedAfterFilter = new RecommendationRequestCreatedAfterFilter();
        RecommendationRequestMessageFilter recommendationRequestMessageFilter = new RecommendationRequestMessageFilter();
        RecommendationRequestStatusFilter recommendationRequestStatusFilter = new RecommendationRequestStatusFilter();
        List<RecommendationRequestFilter> recommendationRequestFilters = List.of(recommendationRequestUpdatedBeforeFilter, recommendationRequestUpdatedAfterFilter,
                recommendationRequestCreatedBeforeFilter, recommendationRequestCreatedAfterFilter, recommendationRequestMessageFilter, recommendationRequestStatusFilter);

        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, recommendationRequestMapper, recommendationRequestFilters);
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

        RecommendationRequestFilterDto requestFilterDto =
                RecommendationRequestFilterDto.builder()
                        .status(RequestStatus.PENDING)
                        .build();

        when(recommendationRequestRepository.findAll()).thenReturn(requests);

        List<RecommendationRequestDto> eventsByFilter = recommendationRequestService.getRecommendationRequests(requestFilterDto);

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

        RecommendationRequestFilterDto requestFilterDto =
                RecommendationRequestFilterDto.builder()
                        .messagePattern("Hel")
                        .status(RequestStatus.ACCEPTED)
                        .build();

        when(recommendationRequestRepository.findAll()).thenReturn(requests);

        List<RecommendationRequestDto> eventsByFilter = recommendationRequestService.getRecommendationRequests(requestFilterDto);

        assertEquals(2, eventsByFilter.size());
        assertEquals("Hello", eventsByFilter.get(0).getMessage());
        assertEquals(RequestStatus.ACCEPTED, eventsByFilter.get(0).getStatus());
    }

}