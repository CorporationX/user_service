package school.faang.user_service.controler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {
    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    public void testRecommendationControllerRecommendationValidationWithNullContent() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent(null);

        assertThrows(DataValidationException.class, () -> recommendationController.giveRecommendation(recommendationDto));
    }

    @Test
    public void testRecommendationControllerRecommendationValidationWithEmptyContent() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("      ");

        assertThrows(DataValidationException.class, () -> recommendationController.giveRecommendation(recommendationDto));
    }

    @Test
    public void testIdValidation() {
        long receiverId = 0;
        int numPage = 0;
        int sizePage = 10;

        assertThrows(DataValidationException.class, () -> recommendationController.getAllUserRecommendations(receiverId, numPage, sizePage));
    }

    @Test
    public void testGiveRecommendationCorrectInput() {
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
        recommendationDto.setContent("content");

        when(recommendationService.update(recommendationDto)).thenReturn(recommendationDto);

        RecommendationDto result = recommendationController.updateRecommendation(recommendationDto);

        verify(recommendationService, times(1)).update(recommendationDto);

        assertEquals(recommendationDto, result);
    }

}
