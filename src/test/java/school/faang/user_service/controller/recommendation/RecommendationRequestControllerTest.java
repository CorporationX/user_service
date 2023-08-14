package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationRequestControllerTest {

    private RecommendationRequestController recommendationRequestController;

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        recommendationRequestController = new RecommendationRequestController(recommendationRequestService);
    }

    @Test
    void requestRecommendationValidRequsetReturnsRecommendationRequestDto(){
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setMessage("Test");

        Mockito.doNothing().when(recommendationRequestService).create(Mockito.any(RecommendationRequestDto.class));

        RecommendationRequestDto result = recommendationRequestController.requestRecommendation(recommendationRequestDto);

        Assertions.assertEquals(recommendationRequestDto, result);
        Mockito.verify(recommendationRequestService, Mockito.times(1)).create(Mockito.any(RecommendationRequestDto.class));
    }

    @Test
    void requestRecommendationEmptyMessageThrowsIllegalArgumentException(){
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setMessage("");
        Assertions.assertThrows(IllegalArgumentException.class, () -> recommendationRequestController.requestRecommendation(recommendationRequestDto),
                "Recommendation request message should not be empty");
        Mockito.verify(recommendationRequestService, Mockito.never()).create(Mockito.any(RecommendationRequestDto.class));
    }

}