package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestValidatorTest {

    @InjectMocks
    private RecommendationRequestValidator recommendationRequestValidator;
    private RecommendationRequestDto rqd;
    private RecommendationRequestDto rqd2;
    private RecommendationRequest rq;
    private static final String VALID_MESSAGE = "Hi ho!";
    private static final RequestStatus REQUEST_STATUS_PENDING = RequestStatus.PENDING;
    private static final LocalDateTime VALID_TIME =
            LocalDateTime.of(2024, Month.FEBRUARY, 2, 15, 50);
    private static final String NULL_MESSAGE = null;
    private static final String EMPTY_MESSAGE = " ";
    private static final LocalDateTime NOT_VALID_TIME =
            LocalDateTime.of(2024, Month.JULY, 2, 15, 50);
    private static final RequestStatus REQUEST_STATUS_REJECTED = RequestStatus.REJECTED;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Validating rqd message not null and previous request time is valid to be more than 6 months")
        public void whenValidDtoMessageThenDoesNotThrowException() {
            rqd = RecommendationRequestDto.builder()
                    .message(VALID_MESSAGE)
                    .createdAt(VALID_TIME)
                    .build();
            recommendationRequestValidator.validateRecommendationRequestMessageNotNull(rqd);
            assertDoesNotThrow(() -> recommendationRequestValidator.validateRecommendationRequestMessageNotNull(rqd));
        }

        @Test
        @DisplayName("Validate request status is pending and doesn't throw exception")
        public void whenRequestStatusIsPendingThenDoNotThrowException() {
            rq = RecommendationRequest.builder()
                    .status(REQUEST_STATUS_PENDING)
                    .build();
            assertDoesNotThrow(() ->
                    recommendationRequestValidator.validateRequestStatus(rq));
        }

        @Test
        @DisplayName("When previous request is exist and was made more than six months ago then don't throw exception")
        public void whenPreviousRequestIsValidThenDoesNotThrowException() {
            rq = RecommendationRequest.builder()
                    .createdAt(VALID_TIME)
                    .status(REQUEST_STATUS_PENDING)
                    .build();
            assertDoesNotThrow(() ->
                    recommendationRequestValidator.validatePreviousRequest(rq));
        }

    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("When message null or empty throw exception")
        public void whenMessageIsBlankOrNullThenThrowException() {
            rqd = RecommendationRequestDto.builder()
                    .message(NULL_MESSAGE)
                    .build();
            rqd2 = RecommendationRequestDto.builder()
                    .message(EMPTY_MESSAGE)
                    .build();

            assertThrows(DataValidationException.class, () -> recommendationRequestValidator
                    .validateRecommendationRequestMessageNotNull(rqd));
            assertThrows(DataValidationException.class, () -> recommendationRequestValidator
                    .validateRecommendationRequestMessageNotNull(rqd2));
        }

        @Test
        @DisplayName("Validate that from previous request hasn't passed 6 months throws exception")
        public void whenPreviousRequestTimeIsLessThanSixMonthsThenThrowException() {
            rq = RecommendationRequest.builder()
                    .message(VALID_MESSAGE)
                    .createdAt(NOT_VALID_TIME)
                    .build();

            assertThrows(DataValidationException.class, () ->
                    recommendationRequestValidator.validatePreviousRequest(rq));
        }

        @Test
        @DisplayName("Test when requestStatus differs from Pending throw exception")
        public void whenRequestStatusIsNotPendingThenThrowException() {
            rq = RecommendationRequest.builder()
                    .status(REQUEST_STATUS_REJECTED)
                    .build();
            assertThrows(DataValidationException.class, () ->
                    recommendationRequestValidator.validateRequestStatus(rq));
        }
    }
}
