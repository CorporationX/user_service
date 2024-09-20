package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {
    @Mock
    private RecommendationRequestService recommendationRequestService;
    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

}
