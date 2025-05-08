package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendationrequest.RecommendationRequestStatusFilter;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestStatusFilterTest {
    @Test
    public void testIsApplicable_ReturnsTrueWhenStatusIsNotNull() {
        RequestFilterDto dto = new RequestFilterDto();
        dto.setStatus(RequestStatus.ACCEPTED);
        RecommendationRequestStatusFilter filter = new RecommendationRequestStatusFilter();

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    public void testIsApplicable_ReturnsFalseWhenStatusIsNull() {
        RequestFilterDto dto = new RequestFilterDto();
        dto.setStatus(null);
        RecommendationRequestStatusFilter filter = new RecommendationRequestStatusFilter();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    public void testApplyFiltersRequestsByStatus() {
        RequestFilterDto dto = new RequestFilterDto();
        dto.setStatus(RequestStatus.ACCEPTED);
        RecommendationRequestStatusFilter filter = new RecommendationRequestStatusFilter();

        RecommendationRequest req1 = new RecommendationRequest();
        req1.setStatus(RequestStatus.ACCEPTED);

        RecommendationRequest req2 = new RecommendationRequest();
        req2.setStatus(RequestStatus.REJECTED);

        RecommendationRequest req3 = new RecommendationRequest();
        req3.setStatus(RequestStatus.ACCEPTED);

        List<RecommendationRequest> requests = Arrays.asList(req1, req2, req3);

        List<RecommendationRequest> result = filter.apply(requests.stream(), dto)
                .toList();

        assertEquals(2, result.size());
        assertTrue(result.contains(req1));
        assertTrue(result.contains(req3));
        assertFalse(result.contains(req2));
    }
}