package school.faang.user_service.service;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.rejection.RejectionDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(value = {MockitoExtension.class})
public class RecommendationRequestServiceTest {
    private final Long userId = 1L;
    private final Long requestId = 1L;

    @Mock
    private RecommendationRequestController recommendationRequestController;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Spy
    private RecommendationRequestMapper recommendationRequestMapper;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    public void getRequestThrowsException() {
        Mockito.when(recommendationRequestRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                recommendationRequestService.getRequest(userId));
    }

    @Test
    void getRequest() {
        Optional<RecommendationRequest> optionalRequest = Optional.of(new RecommendationRequest());
        RecommendationRequest desiredRequest = optionalRequest.get();

        Mockito.when(recommendationRequestRepository.findById(userId))
                .thenReturn(optionalRequest);

        RecommendationRequestDto receiveRequest = recommendationRequestService.getRequest(userId);

        Assert.assertEquals(recommendationRequestMapper.toDto(desiredRequest), receiveRequest);
        verify(recommendationRequestRepository).findById(userId);
    }

    @Test
    void rejectRequestNullRejectionDto() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(requestId, null));

        assertEquals("Rejection and its reason mustn't be null or empty.", exception.getMessage());
    }

    @Test
    void rejectRequestEmptyRejectionReason() {
        RejectionDto rejectionDto = new RejectionDto("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(requestId, rejectionDto));
    }
}
