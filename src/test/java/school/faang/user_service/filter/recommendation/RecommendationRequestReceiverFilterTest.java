package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestReceiverFilterTest {
    @InjectMocks
    private RecommendationRequestReceiverFilter recommendationRequestReceiverFilter;
    @Mock
    private RecommendationRequestFilterDto filters;
    @Mock
    private RecommendationRequest recommendationRequest;

    @BeforeEach
    void setUp() {
        recommendationRequestReceiverFilter = new RecommendationRequestReceiverFilter();
        filters = new RecommendationRequestFilterDto();
        recommendationRequest = new RecommendationRequest();
    }
}
