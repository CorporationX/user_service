package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestStatusFilterTest {
    @InjectMocks
    private RecommendationRequestStatusFilter recommendationRequestStatusFilter;
    @Mock
    private RecommendationRequestFilterDto filter;
    @Mock
    private RecommendationRequest recommendationRequest;

    @BeforeEach
    void setUp() {
        recommendationRequestStatusFilter = new RecommendationRequestStatusFilter();
        filter = new RecommendationRequestFilterDto();
        recommendationRequest = new RecommendationRequest();
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenFilterIsNull() {
        boolean result = recommendationRequestStatusFilter.isApplicable(null);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenFilterReceiverIdIsNull() {
        filter.setRequesterId(null);
        boolean result = recommendationRequestStatusFilter.isApplicable(filter);

        assertFalse(result);
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenFilterHasReceiverId() {
        filter.setStatus(RequestStatus.ACCEPTED);

        boolean result = recommendationRequestStatusFilter.isApplicable(filter);

        assertTrue(result);
    }

    @Test
    void apply_ShouldReturnTrue_WhenUserUsernameMatchesPattern() {
        filter.setStatus(RequestStatus.ACCEPTED);

        List<RecommendationRequest> recommendationRequests = List.of(recommendationRequest);
        Stream<RecommendationRequest> result = recommendationRequestStatusFilter.apply(recommendationRequests.stream(), filter);

//        asse
    }
}
