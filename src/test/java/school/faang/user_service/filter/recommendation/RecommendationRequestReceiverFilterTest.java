package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestReceiverFilterTest {
    @InjectMocks
    private RecommendationRequestReceiverFilter recommendationRequestReceiverFilter;
    @Mock
    private RecommendationRequestFilterDto filter;

    @BeforeEach
    void setUp() {
        recommendationRequestReceiverFilter = new RecommendationRequestReceiverFilter();
        filter = new RecommendationRequestFilterDto();
    }

    // Tests for isApplicable method
    @Test
    void isApplicable_ShouldReturnFalse_WhenFilterIsNull() {
        boolean result = recommendationRequestReceiverFilter.isApplicable(null);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenFilterReceiverIdIsNull() {
        filter.setReceiverId(null);
        boolean result = recommendationRequestReceiverFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenFilterHasReceiverId() {
        filter.setReceiverId(1L);

        boolean result = recommendationRequestReceiverFilter.isApplicable(filter);

        assertTrue(result);
    }

    // Tests for apply method
    @Test
    void apply_ShouldReturnFilteredEmptyStream_WhenNoRecommendationRequestMatchesReceiverId() {
        Stream<RecommendationRequest> recommendationRequests = prepareData();
        filter.setReceiverId(999L);

        List<RecommendationRequest> result = recommendationRequestReceiverFilter.apply(recommendationRequests, filter).toList();

        assertTrue(result.isEmpty());
    }

    @Test
    void apply_ShouldReturnFilteredStream_WhenRecommendationRequestReceiverIdMatches() {
        Stream<RecommendationRequest> recommendationRequests = prepareData();
        filter.setReceiverId(1L);

        List<RecommendationRequest> result = recommendationRequestReceiverFilter.apply(recommendationRequests, filter).toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getReceiver().getId());
    }

    @Test
    void apply_ShouldReturnAllMatchingRequests_WhenMultipleRequestsMatchReceiverId() {
        Stream<RecommendationRequest> recommendationRequests = prepareData();
        filter.setReceiverId(2L);

        List<RecommendationRequest> result = recommendationRequestReceiverFilter.apply(recommendationRequests, filter).toList();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(req -> req.getReceiver().getId().equals(2L)));
    }

    private Stream<RecommendationRequest> prepareData() {
        User receiver1 = new User();
        receiver1.setId(1L);

        User receiver2 = new User();
        receiver2.setId(2L);

        RecommendationRequest request1 = new RecommendationRequest();
        request1.setReceiver(receiver1);

        RecommendationRequest request2 = new RecommendationRequest();
        request2.setReceiver(receiver2);

        RecommendationRequest request3 = new RecommendationRequest();
        request3.setReceiver(receiver2);

        List<RecommendationRequest> recommendationRequests = List.of(request1, request2, request3);

        return recommendationRequests.stream();
    }
}
