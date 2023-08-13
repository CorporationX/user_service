package school.faang.user_service.controller.RecommendationTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.RecommendationController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.utils.validator.RecommendationDtoValidator;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {
    @InjectMocks
    RecommendationController recommendationController;
    @Mock
    RecommendationService recommendationService;
    @Mock
    RecommendationDtoValidator recommendationDtoValidator;

    @Test
    void giveRecommendation() {
        RecommendationDto recommendationDto = RecommendationDto.builder().build();
        recommendationController.giveRecommendation(recommendationDto);
        Mockito.verify(recommendationDtoValidator).validateRecommendation(recommendationDto);
        Mockito.verify(recommendationService).create(recommendationDto);
    }

    @Test
    void updateRecommendation() {
        RecommendationDto recommendationDto = RecommendationDto.builder().build();
        recommendationController.updateRecommendation(recommendationDto);
        Mockito.verify(recommendationDtoValidator).validateRecommendation(recommendationDto);
        Mockito.verify(recommendationService).update(recommendationDto);
    }

    @Test
    void deleteRecommendation() {
        long id = Mockito.anyLong();
        recommendationController.deleteRecommendation(id);
        Mockito.verify(recommendationService).delete(id);
    }
}
