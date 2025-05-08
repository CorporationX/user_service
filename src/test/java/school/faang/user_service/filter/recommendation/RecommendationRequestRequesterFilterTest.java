package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendationrequest.RecommendationRequestRequesterFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationRequestRequesterFilterTest {
    @Test
    public void testIsApplicable_ReturnTrueIfRequesterNotNull() {
        RequestFilterDto dto = new RequestFilterDto();
        User user = new User();
        user.setUsername("requester1");
        dto.setRequester(user);
        RecommendationRequestRequesterFilter filter = new RecommendationRequestRequesterFilter();

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    public void testIsApplicable_ReturnFalseIfRequesterNull() {
        RequestFilterDto dto = new RequestFilterDto();
        dto.setRequester(null);
        RecommendationRequestRequesterFilter filter = new RecommendationRequestRequesterFilter();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    public void testApply_FilterByRequester() {
        User requester = new User();
        requester.setId(1L);
        RequestFilterDto dto = new RequestFilterDto();
        dto.setRequester(requester);
        RecommendationRequestRequesterFilter filter = new RecommendationRequestRequesterFilter();

        RecommendationRequest req1 = new RecommendationRequest();
        req1.setRequester(requester);
        RecommendationRequest req2 = new RecommendationRequest();
        req2.setRequester(new User());
        List<RecommendationRequest> list = List.of(req1, req2);

        List<RecommendationRequest> result = filter.apply(list.stream(), dto)
                .toList();

        assertEquals(1, result.size());
        assertTrue(result.contains(req1));
    }
}