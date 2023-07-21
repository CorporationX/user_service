package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

public class RecommendationRequestControllerTest {
    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    RecommendationRequestDto recommendationRequest = new RecommendationRequestDto();

    @Test
    public void testNullMessageIsInvalid() {
        recommendationRequest.setMessage(null);
        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> recommendationRequestController.requestRecommendation(recommendationRequest)
        );
    }

    @Test
    public void testEmptyMessageIsInvalid() {
        recommendationRequest.setMessage("");
        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> recommendationRequestController.requestRecommendation(recommendationRequest)
        );
    }

    @Test
    public void testRecommendationRequestCreated() {
        recommendationRequestController.requestRecommendation(recommendationRequest);
        Mockito.verify(recommendationRequestService, Mockito.times(1)).create(recommendationRequest);
    }
}
