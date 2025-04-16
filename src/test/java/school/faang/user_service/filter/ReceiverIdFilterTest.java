package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReceiverIdFilterTest {
    private final ReceiverIdFilter receiverIdFilter = new ReceiverIdFilter();

    @Test
    void testIsApplicableWhenReceiverIdIsNotNull() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setReceiverId(20L);

        boolean result = receiverIdFilter.isApplicable(filter);

        assertTrue(result);
    }

    @Test
    void testIsApplicableWhenReceiverIdIsNull() {
        RequestFilterDto filter = new RequestFilterDto();

        boolean result = receiverIdFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    void testIsApplicableWhenReceiverIdIsNegative() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setReceiverId(-1L);

        boolean result = receiverIdFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    void shouldFilterRequestsMatchingReceiverId() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setReceiverId(20L);

        User user1 = new User();
        user1.setId(20L);
        User user2 = new User();
        user2.setId(30L);

        RecommendationRequest request1 = new RecommendationRequest();
        request1.setReceiver(user1);
        RecommendationRequest request2 = new RecommendationRequest();
        request2.setReceiver(user2);

        List<RecommendationRequest> requests = List.of(request1, request2);
        Stream<RecommendationRequest> filteredStream = receiverIdFilter.apply(requests.stream(), filter);
        List<RecommendationRequest> filteredList = filteredStream.toList();

        assertEquals(1, filteredList.size());
        assertEquals(20L, filteredList.get(0).getReceiver().getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoRequestsMatchReceiverId() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setReceiverId(100L);

        User user1 = new User();
        user1.setId(20L);
        User user2 = new User();
        user2.setId(30L);

        RecommendationRequest request1 = new RecommendationRequest();
        request1.setReceiver(user1);
        RecommendationRequest request2 = new RecommendationRequest();
        request2.setReceiver(user2);

        List<RecommendationRequest> requests = List.of(request1, request2);
        Stream<RecommendationRequest> filteredStream = receiverIdFilter.apply(requests.stream(), filter);
        List<RecommendationRequest> filteredList = filteredStream.toList();

        assertEquals(0, filteredList.size());
    }
}