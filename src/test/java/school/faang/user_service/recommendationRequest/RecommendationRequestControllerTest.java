package school.faang.user_service.recommendationRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.controller.recommendation.RecommendationRequestDto;
import school.faang.user_service.controller.recommendation.RejectionDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {

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
        assertThrows(RuntimeException.class, () -> recommendationRequestController.requestRecommendation(requestDto));
    }

    @Test
    public void testRecommendationRequest() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setMessage("something");
        recommendationRequestController.requestRecommendation(requestDto);
        verify(recommendationRequestService, Mockito.times(1)).create(requestDto);
    }

    @Test
    public void testGetRecommendationRequest() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setId(1L);
        requestDto.setSkillsIds(List.of(1L, 2L));
        recommendationRequestController.getRecommendationRequest(1L);
        verify(recommendationRequestService).getRequest(any());
    }

    @Test
    public void testRejectRequest() {
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("Test");
        recommendationRequestController.rejectRequest(1L, rejectionDto);
        verify(recommendationRequestService, times(1)).rejectRequest(1L, rejectionDto);
    }

    @Test
    public void testRejectNullRequest() {
        assertThrows(DataValidationException.class, () -> recommendationRequestController.rejectRequest(1L, null));
    }
}
