package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.RecommendationController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {

    @InjectMocks
    private RecommendationController recommendationController;

    @Mock
    private RecommendationService recommendationService;

    @Test
    public void testGiveRecommendationNullContentIsInvalid() {
        RecommendationDto recommendationDto = new RecommendationDto(1L, 2L, 3L, null, null, null);

        assertThrows(IllegalArgumentException.class,
                () -> recommendationController.giveRecommendation(recommendationDto));
    }

    @Test
    public void testCreateRecommendation(){
        RecommendationDto recommendationDto = new RecommendationDto(1L, 2L, 3L, "content", null, null);
        recommendationController.giveRecommendation(recommendationDto);

        Mockito.verify(recommendationService, Mockito.times(1)).create(recommendationDto);
    }


    @Test
    public void testUpdateRecommendation(){
        RecommendationDto recommendationDto = new RecommendationDto(1L, 2L, 3L, "content", null, null);

        recommendationController.updateRecommendation(recommendationDto);
        Mockito.verify(recommendationService, Mockito.times(1)).update(recommendationDto);
    }

    @Test
    public void testDeleteRecommendation(){
        long id = 1L;
        recommendationService.delete(id);

        Mockito.verify(recommendationService, Mockito.times(1)).delete(id);
    }

    @Test
    public void testGetAllUserRecommendations(){
        long id = 1L;

        recommendationService.getAllUserRecommendations(id);
        Mockito.verify(recommendationService, Mockito.times(1)).getAllUserRecommendations(id);

    }

    @Test
    public void testGetAllRecommendations(){
        long id = 1L;

        recommendationService.getAllGivenRecommendations(id);
        Mockito.verify(recommendationService, Mockito.times(1)).getAllGivenRecommendations(id);
    }


}
