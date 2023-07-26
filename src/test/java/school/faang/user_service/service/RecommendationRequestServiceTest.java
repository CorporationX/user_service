package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    @Test
    @DisplayName("Positive test with correct data for rejectRequest")
    void rejectRequest_test_correctData() {
        RecommendationRequest request;
        request = new RecommendationRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);

        Mockito.when(recommendationRequestRepository
                        .findById(1L))
                .thenReturn(Optional.of(request));

        Mockito.when(recommendationRequestRepository
                        .save(request))
                .thenReturn(request);
        RejectionDto rejection = new RejectionDto(RequestStatus.REJECTED, "Because");
        RecommendationRequestDto requestDto = recommendationRequestService.rejectRequest(1L, rejection);
        assertEquals(1L, requestDto.getId().longValue());
        assertEquals(RequestStatus.REJECTED, requestDto.getStatus());
        assertEquals("Because", requestDto.getRejectionReason());
    }

    @Test
    @DisplayName("Negative test with invalid id")
    void rejectRequest_test_invalidId() {
        Mockito.when(recommendationRequestRepository
                        .findById(23L))
                .thenReturn(Optional.empty());
        RejectionDto rejection = new RejectionDto(RequestStatus.REJECTED, "Because");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.rejectRequest(23L, rejection));
        assertEquals(RecommendationRequestService.REQUEST_NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Negative test with invalid status")
    void rejectRequest_test_invalidStatus() {
        Mockito.when(recommendationRequestRepository
                        .findById(1L))
                .thenReturn(Optional.of(new RecommendationRequest()));
        RejectionDto rejection = new RejectionDto(RequestStatus.REJECTED, "Because");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.rejectRequest(1L, rejection));
        assertEquals(RecommendationRequestService.REQUEST_IS_NOT_PENDING, exception.getMessage());
    }
}