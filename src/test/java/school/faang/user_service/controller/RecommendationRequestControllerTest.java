package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {
    private RequestFilterDto filterDto;
    private RejectionDto rejection;
    private RecommendationRequestDto recommendationRequest;
  
    @Mock
    private RecommendationRequestService recommendationRequestService;
    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @BeforeEach
    void setUp() {
        filterDto = RequestFilterDto.builder()
                .status(RequestStatus.REJECTED)
                .requesterId(4L)
                .receiverId(11L)
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build();

        rejection = RejectionDto.builder()
                .reason("reason")
                .build();

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
    public void testRecommendationRequestsFiltered() {
        recommendationRequestController.getRecommendationRequests(filterDto);
        Mockito.verify(recommendationRequestService, Mockito.times(1)).getRequests(filterDto);
    }
  
    @Test
    public void testSuccessfulRequestGetting() {
        long id = 4;
        recommendationRequestController.getRecommendationRequest(id);
        Mockito.verify(recommendationRequestService, Mockito.times(1)).getRequest(id);
    }
  
    @Test
    public void testNullRejectionReasonIsInvalid() {
        long id = 12;
        rejection.setReason(null);
        Assert.assertThrows(
                DataValidationException.class,
                () -> recommendationRequestController.rejectRequest(id, rejection)
        );
    }

    @Test
    public void testEmptyRejectionReasonIsInvalid() {
        long id = 12;
        rejection.setReason("");
        Assert.assertThrows(
                DataValidationException.class,
                () -> recommendationRequestController.rejectRequest(id, rejection)
        );
    }

    @Test
    public void testRecommendationRequestRejected() {
        long id = 12;
        rejection = RejectionDto.builder().reason("reason").build();
        recommendationRequestController.rejectRequest(id, rejection);
        Mockito.verify(recommendationRequestService, Mockito.times(1)).rejectRequest(id, rejection);
    }
}
