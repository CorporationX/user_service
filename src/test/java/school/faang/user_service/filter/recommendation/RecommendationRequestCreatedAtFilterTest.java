package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendationrequest.RecommendationRequestCreatedAtFilter;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestCreatedAtFilterTest {

    @Test
    public void testIsApplicable_ReturnTrueIfCreatedAtNotNull() {
        RequestFilterDto dto = new RequestFilterDto();
        dto.setCreatedAt(LocalDateTime.now());
        RecommendationRequestCreatedAtFilter filter = new RecommendationRequestCreatedAtFilter();

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    public void testIsApplicable_ReturnFalseIfCreatedAtNull() {
        RequestFilterDto dto = new RequestFilterDto();
        dto.setCreatedAt(null);
        RecommendationRequestCreatedAtFilter filter = new RecommendationRequestCreatedAtFilter();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    public void testApply_FilterByCreatedAt() {
        LocalDateTime threshold = LocalDateTime.of(2023, 10, 1, 0, 0);
        RequestFilterDto dto = new RequestFilterDto();
        dto.setCreatedAt(threshold);
        RecommendationRequestCreatedAtFilter filter = new RecommendationRequestCreatedAtFilter();

        RecommendationRequest req1 = new RecommendationRequest();
        req1.setCreatedAt(LocalDateTime.of(2023, 10, 2, 0, 0));
        RecommendationRequest req2 = new RecommendationRequest();
        req2.setCreatedAt(LocalDateTime.of(2023, 9, 30, 23, 59));
        RecommendationRequest req3 = new RecommendationRequest();
        List<RecommendationRequest> list = List.of(req1, req2, req3);

        List<RecommendationRequest> result = filter.apply(list.stream(), dto)
                .toList();

        assertEquals(1, result.size());
        assertTrue(result.contains(req1));
    }
}