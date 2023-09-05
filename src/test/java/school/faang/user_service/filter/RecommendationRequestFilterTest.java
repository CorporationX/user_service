package school.faang.user_service.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.*;
import java.util.List;

public class RecommendationRequestFilterTest {
    private List<RecommendationRequestFilter> recommendationRequestFilters;
    private RecommendationRequest recommendationRequest;

    @BeforeEach
    void setUp() {
        recommendationRequestFilters = List.of(
                new RecommendationRequestStatusFilter(),
                new RecommendationRequesterIdFilter(),
                new RecommendationReceiverIdFilter()
        );
    }

    @Test
    public void testRecommendationRequestStatusFilter() {
        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setStatus(RequestStatus.REJECTED);
        recommendationRequest2.setStatus(RequestStatus.ACCEPTED);
        recommendationRequest3.setStatus(RequestStatus.REJECTED);

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RequestFilterDto requestFilterDto = RequestFilterDto.builder().status(RequestStatus.REJECTED).build();

        for (RecommendationRequestFilter recommendationRequestFilter : recommendationRequestFilters) {
            if (recommendationRequestFilter.isApplicable(requestFilterDto)) {
                requests = recommendationRequestFilter.apply(requests.stream(), requestFilterDto).toList();
            }
        }

        Assertions.assertEquals(2, requests.size());
        Assertions.assertEquals(RequestStatus.REJECTED, requests.get(0).getStatus());
        Assertions.assertEquals(RequestStatus.REJECTED, requests.get(1).getStatus());
    }

    @Test
    public void testRecommendationRequestReceiverIdFilter() {
        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setReceiver(User.builder().id(2L).build());
        recommendationRequest2.setReceiver(User.builder().id(1L).build());
        recommendationRequest3.setReceiver(User.builder().id(1L).build());

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RequestFilterDto requestFilterDto = RequestFilterDto.builder().receiverId(1L).build();

        for (RecommendationRequestFilter recommendationRequestFilter : recommendationRequestFilters) {
            if (recommendationRequestFilter.isApplicable(requestFilterDto)) {
                requests = recommendationRequestFilter.apply(requests.stream(), requestFilterDto).toList();
            }
        }

        Assertions.assertEquals(2, requests.size());
        Assertions.assertEquals(1L, requests.get(0).getReceiver().getId());
        Assertions.assertEquals(1L, requests.get(1).getReceiver().getId());
    }

    @Test
    public void testGetRequestsWithManyFilters() {
        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();

        recommendationRequest1.setStatus(RequestStatus.REJECTED);
        recommendationRequest1.setRequester(User.builder().id(2L).build());
        recommendationRequest1.setReceiver(User.builder().id(1L).build());

        recommendationRequest2.setStatus(RequestStatus.ACCEPTED);
        recommendationRequest2.setRequester(User.builder().id(1L).build());
        recommendationRequest2.setReceiver(User.builder().id(2L).build());

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2);

        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .status(RequestStatus.ACCEPTED)
                .requesterId(1L)
                .receiverId(2L)
                .build();

        for (RecommendationRequestFilter recommendationRequestFilter : recommendationRequestFilters) {
            if (recommendationRequestFilter.isApplicable(requestFilterDto)) {
                requests = recommendationRequestFilter.apply(requests.stream(), requestFilterDto).toList();
            }
        }

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(RequestStatus.ACCEPTED, requests.get(0).getStatus());
        Assertions.assertEquals(1L, requests.get(0).getRequester().getId());
        Assertions.assertEquals(2L, requests.get(0).getReceiver().getId());
    }
}
