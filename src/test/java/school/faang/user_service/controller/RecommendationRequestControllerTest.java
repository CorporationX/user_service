package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;
import java.time.LocalDateTime;

public class RecommendationRequestControllerTest {
    private RejectionDto rejection;
    private RecommendationRequestDto recommendationRequest;
    @Mock
    private RecommendationRequestService recommendationRequestService;
    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @BeforeEach
    void setUp() {
        recommendationRequest = RecommendationRequestDto.builder()
                .id(5L)
                .message("message")
                .status(RequestStatus.REJECTED)
                .skills(null)
                .requesterId(4L)
                .receiverId(11L)
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build();
    }

    @Test
    public void testNullMessageIsInvalid() {
        recommendationRequest.setMessage(null);
        Assert.assertThrows(
                DataValidationException.class,
                () -> recommendationRequestController.requestRecommendation(recommendationRequest)
        );
    }

    @Test
    public void testEmptyMessageIsInvalid() {
        recommendationRequest.setMessage("");
        Assert.assertThrows(
                DataValidationException.class,
                () -> recommendationRequestController.requestRecommendation(recommendationRequest)
        );
    }

    @Test
    public void testRecommendationRequestCreated() {
        recommendationRequestController.requestRecommendation(recommendationRequest);
        Mockito.verify(recommendationRequestService, Mockito.times(1)).create(recommendationRequest);
    }

    @Test
    public void testRecommendationRequestRejected() {
        long id = 12;
        rejection = RejectionDto.builder().reason("reason").build();
        recommendationRequestController.requestRecommendation(recommendationRequest);
        Mockito.verify(recommendationRequestService, Mockito.times(1)).rejectRequest(id, rejection);
    }
}
