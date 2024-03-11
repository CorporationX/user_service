package school.faang.user_service.service.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationController;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.service.recomendation.RecommendationService;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {
    @InjectMocks
    private RecommendationController recommendationController;
    @Mock
    private RecommendationService recommendationService;

    @Test
    void testRecommendationIsCreated() {
        RecommendationDto recommendationDto = new RecommendationDto(1L, 2L, 3L, "any");
        recommendationController.create(recommendationDto);
        verify(recommendationService, times(1)).create(recommendationDto);
    }
}
