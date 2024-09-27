package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestStatusFilterTest {
    @InjectMocks
    private RecommendationRequestStatusFilter recommendationRequestStatusFilter;
    @Mock
    private RecommendationRequestFilterDto filter;
    @Mock
    private RecommendationRequest recommendationRequest;

    @BeforeEach
    void setUp() {
        recommendationRequestStatusFilter = new RecommendationRequestStatusFilter();
        filter = new RecommendationRequestFilterDto();
        recommendationRequest = new RecommendationRequest();
    }

    // Tests for isApplicable method
    @Test
    void isApplicable_ShouldReturnFalse_WhenFilterIsNull() {
        boolean result = recommendationRequestStatusFilter.isApplicable(null);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenFilterStatusIsNull() {
        filter.setStatus(null);
        boolean result = recommendationRequestStatusFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenFilterHasStatus() {
        filter.setStatus(RequestStatus.ACCEPTED);

        boolean result = recommendationRequestStatusFilter.isApplicable(filter);

        assertTrue(result);
    }

    // Tests for apply method
    @Test
    void apply_ShouldReturnFilteredEmptyStream_WhenNoRecommendationRequestMatchesStatus() {
        Stream<RecommendationRequest> recommendationRequests = prepareData();
        filter.setStatus(RequestStatus.REJECTED);

        List<RecommendationRequest> result = recommendationRequestStatusFilter.apply(recommendationRequests, filter).toList();

        assertTrue(result.isEmpty());
    }

    @Test
    void apply_ShouldReturnFilteredStream_WhenRecommendationRequestStatusMatchesStatus() {
        Stream<RecommendationRequest> recommendationRequests = prepareData();
        filter.setStatus(RequestStatus.ACCEPTED);

        List<RecommendationRequest> result = recommendationRequestStatusFilter.apply(recommendationRequests, filter).toList();

        assertEquals(1, result.size());
        assertEquals(RequestStatus.ACCEPTED, result.get(0).getStatus());
    }

    @Test
    void apply_ShouldReturnAllMatchingRequests_WhenMultipleRequestsMatchStatus() {
        Stream<RecommendationRequest> recommendationRequests = prepareData();
        filter.setStatus(RequestStatus.PENDING);

        List<RecommendationRequest> result = recommendationRequestStatusFilter.apply(recommendationRequests, filter).toList();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(req -> req.getStatus().equals(RequestStatus.PENDING)));
    }


    private Stream<RecommendationRequest> prepareData() {
        RecommendationRequest acceptedRequest = new RecommendationRequest();
        acceptedRequest.setStatus(RequestStatus.ACCEPTED);
        RecommendationRequest pendingRequest1 = new RecommendationRequest();
        pendingRequest1.setStatus(RequestStatus.PENDING);
        RecommendationRequest pendingRequest2 = new RecommendationRequest();
        pendingRequest2.setStatus(RequestStatus.PENDING);

        List<RecommendationRequest> recommendationRequests = List.of(acceptedRequest, pendingRequest1, pendingRequest2);

        return recommendationRequests.stream();
    }
}
