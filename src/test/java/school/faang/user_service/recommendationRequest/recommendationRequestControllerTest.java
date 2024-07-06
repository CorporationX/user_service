package school.faang.user_service.recommendationRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.controller.recommendation.RecommendationRequestDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.RecommendationRequestService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class recommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @Test
    public void testRecommendationRequestMessageNotEmpty() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setMessage("");
        assertThrows(DataValidationException.class, () -> recommendationRequestController.requestRecommendation(requestDto));
    }

    @Test
    public void testRecommendationRequest() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setMessage("something");
        recommendationRequestController.requestRecommendation(requestDto);
        verify(recommendationRequestService, Mockito.times(1)).create(requestDto);

    }
}
