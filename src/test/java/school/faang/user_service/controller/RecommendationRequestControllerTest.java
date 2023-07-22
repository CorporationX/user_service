package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.service.RecommendationRequestService;

public class RecommendationRequestControllerTest {
    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @Test
    public void testSuccessfulRequestGetting() {
        long id = 4;
        recommendationRequestController.getRecommendationRequest(id);
        Mockito.verify(recommendationRequestService, Mockito.times(1)).getRequest(id);
    }
}