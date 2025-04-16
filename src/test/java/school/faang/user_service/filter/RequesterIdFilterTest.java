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

public class RequesterIdFilterTest {
    private final RequesterIdFilter requesterIdFilter = new RequesterIdFilter();

    @Test
    void testIsApplicableWhenRequesterIdIsNotNull() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setRequesterId(10L);

        boolean result = requesterIdFilter.isApplicable(filter);

        assertTrue(result);
    }

    @Test
    void testIsApplicableWhenRequesterIdIsNull() {
        RequestFilterDto filter = new RequestFilterDto();

        boolean result = requesterIdFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    void testIsApplicableWhenRequesterIdIsNegative() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setRequesterId(-1L);

        boolean result = requesterIdFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    void shouldFilterRequestsMatchingRequesterId() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setRequesterId(10L);

        User user1 = new User();
        user1.setId(10L);
        User user2 = new User();
        user2.setId(20L);

        RecommendationRequest request1 = new RecommendationRequest();
        request1.setRequester(user1);
        RecommendationRequest request2 = new RecommendationRequest();
        request2.setRequester(user2);

        List<RecommendationRequest> requests = List.of(request1, request2);
        Stream<RecommendationRequest> filteredStream = requesterIdFilter.apply(requests.stream(), filter);
        List<RecommendationRequest> filteredList = filteredStream.toList();

        assertEquals(1, filteredList.size());
        assertEquals(10L, filteredList.get(0).getRequester().getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoRequestsMatchRequesterId() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setRequesterId(100L);

        User user1 = new User();
        user1.setId(20L);
        User user2 = new User();
        user2.setId(30L);

        RecommendationRequest request1 = new RecommendationRequest();
        request1.setRequester(user1);
        RecommendationRequest request2 = new RecommendationRequest();
        request2.setRequester(user2);

        List<RecommendationRequest> requests = List.of(request1, request2);
        Stream<RecommendationRequest> filteredStream = requesterIdFilter.apply(requests.stream(), filter);
        List<RecommendationRequest> filteredList = filteredStream.toList();

        assertEquals(0, filteredList.size());
    }
}
