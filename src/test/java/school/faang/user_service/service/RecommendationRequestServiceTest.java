package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import java.util.List;

public class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    void testRecommendationRequestStatusFilter() {
        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setStatus(RequestStatus.REJECTED);
        recommendationRequest2.setStatus(RequestStatus.ACCEPTED);
        recommendationRequest3.setStatus(RequestStatus.REJECTED);

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RequestFilterDto requestFilterDto = RequestFilterDto.builder().status(RequestStatus.REJECTED).build();

        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(requests);

        List<RecommendationRequestDto> eventsByFilter = recommendationRequestService.getRequests(requestFilterDto);

        Assertions.assertEquals(2, eventsByFilter.size());
        Assertions.assertEquals(RequestStatus.REJECTED, eventsByFilter.get(0).getStatus());
        Assertions.assertEquals(RequestStatus.REJECTED, eventsByFilter.get(1).getStatus());
    }
}
