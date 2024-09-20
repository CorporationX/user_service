package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
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
    @Mock
    private RecommendationRequest recommendationRequest;

    @BeforeEach
    void setUp() {
        recommendationRequestReceiverFilter = new RecommendationRequestReceiverFilter();
        filter = new RecommendationRequestFilterDto();
        recommendationRequest = new RecommendationRequest();
    }

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

    @Test
    void apply_ShouldReturnTrue_WhenUserUsernameMatchesPattern() {
        filter.setReceiverId(1L);

        List<RecommendationRequest> recommendationRequests = List.of(recommendationRequest);
        Stream<RecommendationRequest> result = recommendationRequestReceiverFilter.apply(recommendationRequests.stream(), filter);

//        assert
    }
}
