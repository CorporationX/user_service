package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    @ParameterizedTest
    @MethodSource("getIdAndRejectReason")
    @DisplayName("Request not found")
    void requestNotFound(long id) {
        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(id, new RejectionDto()));
        assertEquals("Recommendation request not found", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("getStatus")
    @DisplayName("Request already rejected")
    void requestAlreadyRejected(RequestStatus status) {
        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(status);

        when(recommendationRequestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(1L, new RejectionDto()));
        assertEquals("Recommendation request already rejected", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("getIdAndRejectReason")
    @DisplayName("Request rejected")
    void requestRejected(long id, String rejectionReason) {
        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.PENDING);
        request.setId(id);

        RejectionDto reject = new RejectionDto();
        reject.setReason(rejectionReason);
        when(recommendationRequestRepository.findById(id))
                .thenReturn(Optional.of(request));
        when(recommendationRequestRepository.save(request))
                .thenReturn(request);

        RecommendationRequestDto requestDto = recommendationRequestService.rejectRequest(id, reject);
        assertAll(() -> {
                    assertEquals(id, requestDto.getId());
                    assertEquals(RequestStatus.REJECTED, requestDto.getStatus());
                    assertEquals(rejectionReason, requestDto.getRejectionReason());
                }
        );
    }

    private static Stream<Arguments> getStatus() {
        return Stream.of(
                Arguments.of(RequestStatus.ACCEPTED),
                Arguments.of(RequestStatus.REJECTED)
        );
    }

    private static Stream<Arguments> getIdAndRejectReason() {
        return Stream.of(
                Arguments.of(1L, "already exist"),
                Arguments.of(2L, "because"),
                Arguments.of(29L, "Dont need")
        );
    }
}