package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @Test
    void testRequestRecommendationWrongArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.requestRecommendation(null));
    }

    @Test
    void testRequestRecommendationIsOk(){
        RecommendationRequestDto recommendationRequestDto = RecommendationRequestDto.builder().build();
        when(recommendationRequestService.create(recommendationRequestDto)).thenReturn(recommendationRequestDto);

        RecommendationRequestDto responseDto = recommendationRequestController
                .requestRecommendation(recommendationRequestDto);

        verify(recommendationRequestService, times(1)).create(recommendationRequestDto);

        assertEquals(recommendationRequestDto, responseDto);
    }

    @Test
    void testGetRecommendationRequestsWithEmptyFilter() {
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.getRecommendationRequests(null));
    }

    @Test
    void testGetRecommendationRequestsOk() {
        recommendationRequestService.getRequests(Mockito.any());

        verify(recommendationRequestService, times(1)).getRequests(Mockito.any());
    }
}
