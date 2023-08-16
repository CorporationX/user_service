package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = RecommendationRequestMapper.INSTANCE;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    void rejectRequestValidData() {
        long requestId = 1;
        String rejectionReason = "Not suitable for the position";

        RejectionDto rejectionDto = new RejectionDto(rejectionReason);

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(requestId);
        recommendationRequest.setStatus(RequestStatus.PENDING);

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(recommendationRequest));
        RecommendationRequestDto result = recommendationRequestService.rejectRequest(requestId, rejectionDto);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals(RequestStatus.REJECTED, recommendationRequest.getStatus());
        assertEquals(rejectionReason, recommendationRequest.getRejectionReason());
        verify(recommendationRequestRepository).findById(requestId);
    }

    @Test
    void rejectRequestInvalidRequest() {
        long requestId = 1;
        String rejectionReason = "Not suitable for the position";

        RejectionDto rejectionDto = new RejectionDto(rejectionReason);

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(requestId, rejectionDto));

        assertEquals("Recommendation with id: " + requestId + " does not exist", exception.getMessage());
        verify(recommendationRequestRepository).findById(requestId);
    }

    @Test
    void rejectRequestNullRejectionDto() {
        long requestId = 1;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(requestId, null));

        assertEquals("RejectionDto cannot be null or empty", exception.getMessage());
    }

    @Test
    void rejectRequestEmptyRejectionReason() {
        long requestId = 1;
        RejectionDto rejectionDto = new RejectionDto("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(requestId, rejectionDto));

        assertEquals("RejectionDto cannot be null or empty", exception.getMessage());
    }

}