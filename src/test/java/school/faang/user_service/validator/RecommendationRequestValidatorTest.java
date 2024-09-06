package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.recomendation.RecommendationRequestNotValidException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestValidatorTest {
    @InjectMocks
    private RecommendationRequestValidator recommendationRequestValidator;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Test
    public void nullOrEmptyRequestsMessageThrowsExceptionTest() {
        RecommendationRequestDto rqdNullMessage = new RecommendationRequestDto();
        RecommendationRequestDto rqdEmptyMessage = new RecommendationRequestDto();
        rqdNullMessage.setMessage(null);
        rqdEmptyMessage.setMessage("  ");

        assertThrows(RecommendationRequestNotValidException.class, () -> recommendationRequestValidator
                .isRecommendationRequestMessageNull(rqdNullMessage));
        assertThrows(RecommendationRequestNotValidException.class, () -> recommendationRequestValidator
                .isRecommendationRequestMessageNull(rqdEmptyMessage));
    }

    @Test
    public void validMessageDoesNotThrowExceptionTest() {
        RecommendationRequestDto rqdValid = new RecommendationRequestDto();
        rqdValid.setMessage("Hi ho!");

        assertDoesNotThrow(() -> recommendationRequestValidator.isRecommendationRequestMessageNull(rqdValid));
    }

    @Test
    public void requesterAndReceiverExistsPositiveTest() {
        RecommendationRequestDto rqd = new RecommendationRequestDto();
        when(recommendationRequestRepository.findById(rqd.getRequesterId()))
                .thenReturn(Optional.of(new RecommendationRequest()));
        when(recommendationRequestRepository.findById(rqd.getReceiverId()))
                .thenReturn(Optional.of(new RecommendationRequest()));

        assertDoesNotThrow(() -> recommendationRequestValidator.validateRequesterAndReceiverExists(rqd));
    }

    @Test
    public void requesterAndReceiverExistsNegativeTest() {
        RecommendationRequestDto rqd = new RecommendationRequestDto();
        when(recommendationRequestRepository.findById(rqd.getRequesterId()))
                .thenReturn(Optional.of(new RecommendationRequest()));
        when(recommendationRequestRepository.findById(rqd.getReceiverId()))
                .thenReturn(Optional.empty());

        assertThrows(RecommendationRequestNotValidException.class, () ->
                recommendationRequestValidator.validateRequesterAndReceiverExists(rqd));
    }

    @Test
    public void validateRecommendationRequestExistsDoNotThrowExceptionTest() {
        RecommendationRequestDto rqd = new RecommendationRequestDto();
        rqd.setId(1L);
        when(recommendationRequestRepository.findById(rqd.getId())).thenReturn(Optional.of(new RecommendationRequest()));

        assertDoesNotThrow(() -> recommendationRequestValidator.validateRecommendationRequestExists(rqd.getId()));
    }

    @Test
    public void validateRecommendationRequestExistsThrowExceptionTest() {
        RecommendationRequestDto rqd = new RecommendationRequestDto();
        rqd.setId(1L);

        when(recommendationRequestRepository.findById(rqd.getId())).thenReturn(Optional.empty());

        assertThrows(RecommendationRequestNotValidException.class, () ->
                recommendationRequestValidator.validateRecommendationRequestExists(rqd.getId()));
    }

    @Test
    public void validatePreviousRequestDoNotThrowExceptionTest() {
        RecommendationRequestDto rqd = new RecommendationRequestDto();
        rqd.setRequesterId(1L);
        rqd.setReceiverId(2L);

        Optional<RecommendationRequest> optionalRecommendationRequest = Optional.of(new RecommendationRequest());
        when(recommendationRequestRepository.findLatestPendingRequest(rqd.getRequesterId(), rqd.getReceiverId()))
                .thenReturn(optionalRecommendationRequest);

        optionalRecommendationRequest.get().setCreatedAt(LocalDateTime.of(2024, Month.FEBRUARY, 2, 15, 50));

        assertDoesNotThrow(() -> recommendationRequestValidator.validatePreviousRequest(rqd));
    }

    @Test
    public void validatePreviousRequestThrowExceptionTest() {
        RecommendationRequestDto rqd = new RecommendationRequestDto();
        rqd.setRequesterId(1L);
        rqd.setReceiverId(2L);

        Optional<RecommendationRequest> optionalRecommendationRequest = Optional.of(new RecommendationRequest());
        when(recommendationRequestRepository.findLatestPendingRequest(rqd.getRequesterId(), rqd.getReceiverId()))
                .thenReturn(optionalRecommendationRequest);

        optionalRecommendationRequest.get()
                .setCreatedAt(LocalDateTime.of(2024, Month.JULY, 2, 15, 50));

        assertThrows(RecommendationRequestNotValidException.class, () ->
                recommendationRequestValidator.validatePreviousRequest(rqd));
    }

    @Test
    public void validateRequestStatusNotAcceptedOrDeclinedPositiveTest() {
        RecommendationRequest rq = new RecommendationRequest();
        rq.setId(1L);
        rq.setStatus(RequestStatus.PENDING);

        when(recommendationRequestRepository.findById(rq.getId())).thenReturn(Optional.of(rq));

        assertDoesNotThrow(() -> recommendationRequestValidator.validateRequestStatusNotAcceptedOrDeclined(rq.getId()));
    }

    @Test
    public void validateRequestStatusNotAcceptedOrDeclinedThrowsException() {
        RecommendationRequest rq = new RecommendationRequest();
        rq.setId(1L);
        rq.setStatus(RequestStatus.ACCEPTED);

        when(recommendationRequestRepository.findById(rq.getId())).thenReturn(Optional.of(rq));

        assertThrows(RecommendationRequestNotValidException.class, () ->
                recommendationRequestValidator.validateRequestStatusNotAcceptedOrDeclined(rq.getId()));
    }
}
