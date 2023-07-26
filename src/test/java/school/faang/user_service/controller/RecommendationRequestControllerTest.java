package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.rejection.RejectionDto;
import school.faang.user_service.service.RecommendationRequestService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(value = {MockitoExtension.class})
public class RecommendationRequestControllerTest {
    private final Long userId = 1L;
    private final Long requestId = 1L;
    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @Test
    public void testGetRecommendationRequest() {
        RecommendationRequestDto expectedResult = new RecommendationRequestDto();

        when(recommendationRequestService.getRequest(userId)).thenReturn(expectedResult);

        RecommendationRequestDto actualResult = recommendationRequestController.getRecommendationRequest(userId);

        Assert.assertEquals(expectedResult, actualResult);
        Mockito.verify(recommendationRequestService).getRequest(userId);
    }

    @Test
    public void testRejectRequest() {
        RejectionDto rejectionDto = new RejectionDto("");
        RecommendationRequestDto expectedResponse = new RecommendationRequestDto();
        Mockito.when(recommendationRequestService.rejectRequest(requestId, rejectionDto)).thenReturn(expectedResponse);

        RecommendationRequestDto actualResponse = recommendationRequestController.rejectRequest(requestId, rejectionDto);

        Mockito.verify(recommendationRequestService).rejectRequest(requestId, rejectionDto);

        assertEquals(expectedResponse, actualResponse);
    }
}