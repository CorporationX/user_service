package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.RecommendationService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {
    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    public void testMethod() {
    recommendationController.getAllGivenRecommendations(1L);
        verify(recommendationService, times(1)).getAllGivenRecommendations(1L);
    }

}