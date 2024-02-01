package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.controller.RecommendationController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    public void deleteRecommendationTest() {
        long id = 1L;
        RecommendationDto expectedDto = new RecommendationDto();
        when(recommendationService.delete(id)).thenReturn(expectedDto);

        RecommendationDto actualDto = recommendationController.deleteRecommendation(id);

        verify(recommendationService).delete(id);
        assertEquals(expectedDto, actualDto);
    }
}