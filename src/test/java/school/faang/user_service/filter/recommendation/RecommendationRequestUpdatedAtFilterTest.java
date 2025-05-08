package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendationrequest.RecommendationRequestUpdatedAtFilter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestUpdatedAtFilterTest {
    @Test
    public void testIsApplicable_ReturnsTrueWhenUpdatedAtIsNotNull() {
        RequestFilterDto dto = new RequestFilterDto();
        dto.setUpdatedAt(LocalDateTime.now());
        RecommendationRequestUpdatedAtFilter filter = new RecommendationRequestUpdatedAtFilter();

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    public void testIsApplicable_ReturnsFalseWhenUpdatedAtIsNull() {
        RequestFilterDto dto = new RequestFilterDto();
        dto.setUpdatedAt(null);
        RecommendationRequestUpdatedAtFilter filter = new RecommendationRequestUpdatedAtFilter();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    public void testApplyFiltersRequestsByUpdatedAt() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);
        RequestFilterDto dto = new RequestFilterDto();
        dto.setUpdatedAt(threshold);
        RecommendationRequestUpdatedAtFilter filter = new RecommendationRequestUpdatedAtFilter();


        RecommendationRequest req1 = new RecommendationRequest();
        req1.setUpdatedAt(LocalDateTime.now());

        RecommendationRequest req2 = new RecommendationRequest();
        req2.setUpdatedAt(LocalDateTime.now().minusDays(2));

        RecommendationRequest req3 = new RecommendationRequest();
        req3.setUpdatedAt(null);

        List<RecommendationRequest> requests = Arrays.asList(req1, req2, req3);

        List<RecommendationRequest> result = filter.apply(requests.stream(), dto)
                .toList();

        assertEquals(1, result.size());
        assertTrue(result.contains(req1));
        assertFalse(result.contains(req2));
        assertFalse(result.contains(req3));
    }
}