package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendationrequest.RecommendationRequestReceiverFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationRequestReceiverFilterTest {

    @Test
    public void testIsApplicable_ReturnTrueIfReceiverNotNull() {
        RequestFilterDto dto = new RequestFilterDto();
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        dto.setReceiver(user);
        RecommendationRequestReceiverFilter filter = new RecommendationRequestReceiverFilter();

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    public void testIsApplicable_ReturnFalseIfReceiverNull() {
        RequestFilterDto dto = new RequestFilterDto();
        dto.setReceiver(null);
        RecommendationRequestReceiverFilter filter = new RecommendationRequestReceiverFilter();

        assertFalse(filter.isApplicable(dto));
    }

    @Test
    public void testApply_FilterByReceiver() {
        User receiver = new User();
        receiver.setId(1L);
        RequestFilterDto dto = new RequestFilterDto();
        dto.setReceiver(receiver);
        RecommendationRequestReceiverFilter filter = new RecommendationRequestReceiverFilter();

        RecommendationRequest req1 = new RecommendationRequest();
        req1.setReceiver(receiver);
        RecommendationRequest req2 = new RecommendationRequest();
        req2.setReceiver(new User());
        List<RecommendationRequest> list = List.of(req1, req2);

        List<RecommendationRequest> result = filter.apply(list.stream(), dto)
                .toList();

        assertEquals(1, result.size());
        assertTrue(result.contains(req1));
    }
}