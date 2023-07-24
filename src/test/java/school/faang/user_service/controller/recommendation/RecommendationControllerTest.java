package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {
    @InjectMocks
    private RecommendationController recommendationController;
    @Mock
    private RecommendationService recommendationService;

    @Test
    public void testGiveRecommendation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("content");

        when(recommendationService.create(recommendationDto)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationController.giveRecommendation(recommendationDto);

        verify(recommendationService, times(1)).create(recommendationDto);
        assertEquals(recommendationDto, result);
    }

    @Test
    public void testUpdateRecommendation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("Sample content");

        when(recommendationService.update(recommendationDto)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationController.updateRecommendation(recommendationDto);

        verify(recommendationService, times(1)).update(recommendationDto);
        assertEquals(recommendationDto, result);
    }

    @Test
    public void testGiveRecommendation_blankContent_throwException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("   ");

        assertThrows(DataValidationException.class,
                () -> recommendationController.giveRecommendation(recommendationDto));
    }

    @Test
    public void testGiveRecommendation_nullContent_throwException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent(null);

        assertThrows(DataValidationException.class,
                () -> recommendationController.giveRecommendation(recommendationDto));
    }

    @Test
    public void testUpdateRecommendation_blankContent_throwException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("   ");

        assertThrows(DataValidationException.class,
                () -> recommendationController.updateRecommendation(recommendationDto));
    }

    @Test
    public void testUpdateRecommendation_nullContent_throwException() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent(null);

        assertThrows(DataValidationException.class,
                () -> recommendationController.updateRecommendation(recommendationDto));
    }
}
