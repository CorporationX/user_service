package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @Test
    public void testRequestRecommendationSuccess() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setMessage("Some message");

        Mockito.when(recommendationRequestService.create(any(RecommendationRequestDto.class))).thenReturn(requestDto);

        RecommendationRequestDto result = recommendationRequestController.requestRecommendation(requestDto);

        assertEquals(requestDto, result);
        Mockito.verify(recommendationRequestService).create(any(RecommendationRequestDto.class));
    }

}
