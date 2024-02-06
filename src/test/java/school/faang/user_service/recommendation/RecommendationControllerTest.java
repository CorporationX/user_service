package school.faang.user_service.recommendation;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.RecommendationController;
import school.faang.user_service.service.RecommendationService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {
    @Mock
    private RecommendationService recommendationService;
    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    public void testDeleteRecommendation() {
        long recommendationId = 1L;
        recommendationController.deleteRecommendation(recommendationId);
        verify(recommendationController, times(5)).deleteRecommendation(recommendationId);
    }
}
