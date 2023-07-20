package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    RecommendationRequestDto recommendationRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recommendationRequestDto = new RecommendationRequestDto(1, "message", RequestStatus.ACCEPTED, new ArrayList<>(), 2, 5, LocalDateTime.now(), null);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
    }

    @Test
    @DisplayName("Requester and receiver exist")
    void requesterAndReceiverExist_test() {
        Assertions.assertDoesNotThrow(() -> recommendationRequestService.create(recommendationRequestDto));
        Mockito.verify(userRepository, Mockito.times(2)).findById(Mockito.anyLong());
    }

    @Test
    @DisplayName("Requester and receiver not exist")
    void requesterAndReceiverNotExist_test() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(recommendationRequestDto));
        Assertions.assertEquals(exception.getMessage(), RecommendationRequestService.getREQUESTER_OR_RECEIVER_NOT_FOUND());
    }

    @Test
    @DisplayName("Between requests less than 6 months. Must be error")
    void betweenRequestsLessThan6Months_test() {
        RecommendationRequest request = new RecommendationRequest();
        request.setUpdatedAt(recommendationRequestDto.getCreatedAt().minus(5, ChronoUnit.MONTHS));
        Mockito.when(recommendationRepository.findLatestPendingRequest(2, 5)).thenReturn(Optional.of(request));
        DateTimeException exception = Assertions.assertThrows(DateTimeException.class, () -> recommendationRequestService.create(recommendationRequestDto));
        Assertions.assertEquals(exception.getMessage(), RecommendationRequestService.getREQUEST_IS_PENDING());
    }

}