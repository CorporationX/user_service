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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestRequesterFilterTest {
    @InjectMocks
    private RecommendationRequestRequesterFilter recommendationRequestRequesterFilter;
    @Mock
    private RecommendationRequestFilterDto filter;
    @Mock
    private RecommendationRequest recommendationRequest;

    @BeforeEach
    void setUp() {
        recommendationRequestRequesterFilter = new RecommendationRequestRequesterFilter();
        filter = new RecommendationRequestFilterDto();
        recommendationRequest = new RecommendationRequest();
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenFilterIsNull() {
        boolean result = recommendationRequestRequesterFilter.isApplicable(null);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenFilterReceiverIdIsNull() {
        filter.setRequesterId(null);
        boolean result = recommendationRequestRequesterFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenFilterHasReceiverId() {
        filter.setRequesterId(1L);

        boolean result = recommendationRequestRequesterFilter.isApplicable(filter);

        assertTrue(result);
    }

    @Test
    void apply_ShouldReturnTrue_WhenUserUsernameMatchesPattern() {
        filter.setRequesterId(1L);

        List<RecommendationRequest> recommendationRequests = List.of(recommendationRequest);
        Stream<RecommendationRequest> result = recommendationRequestRequesterFilter.apply(recommendationRequests.stream(), filter);

//        asse
    }
}
