package school.faang.user_service.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.recomendation.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestValidatorTest {

    @InjectMocks
    private RecommendationRequestValidator recommendationRequestValidator;
    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    private RecommendationRequestService service;
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
        public void whenValidDtoMessageAndPreviousRequestIsInTimeRanksThenDoesNotThrowException() {
            rqd = RecommendationRequestDto.builder()
                    .message(VALID_MESSAGE)
                    .createdAt(VALID_TIME)
                    .build();

            rq = RecommendationRequest.builder()
                    .message(VALID_MESSAGE)
                    .createdAt(VALID_TIME)
                    .build();
            when(recommendationRequestMapper.toEntity(rqd)).thenReturn(rq);
            when(service.getLastPendingRequest(rq)).thenReturn(rq);
            recommendationRequestValidator.validateRecommendationRequest(rqd);
            assertDoesNotThrow(() -> recommendationRequestValidator.validateRecommendationRequest(rqd));
        }

        @Test
        @DisplayName("Validate request status is pending and doesn't throw exception")
        public void whenRequestStatusIsPendingThenDoNotThrowException() {
            rq = RecommendationRequest.builder()
                    .status(REQUEST_STATUS_PENDING)
                    .build();
            assertDoesNotThrow(() ->
                    recommendationRequestValidator.validateRequestStatusNotAcceptedOrDeclined(rq));
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
                    .validateRecommendationRequest(rqd));
            assertThrows(DataValidationException.class, () -> recommendationRequestValidator
                    .validateRecommendationRequest(rqd2));
        }

        @Test
        @DisplayName("Validate that from previous request hasn't passed 6 months throws exception")
        public void whenPreviousRequestIsInvalidThenThrowException() {
            rqd = RecommendationRequestDto.builder()
                    .message(VALID_MESSAGE)
                    .createdAt(NOT_VALID_TIME)
                    .build();

            rq = RecommendationRequest.builder()
                    .message(VALID_MESSAGE)
                    .createdAt(NOT_VALID_TIME)
                    .build();
            when(recommendationRequestMapper.toEntity(rqd)).thenReturn(rq);
            when(service.getLastPendingRequest(rq)).thenReturn(rq);

            assertThrows(DataValidationException.class, () ->
                    recommendationRequestValidator.validateRecommendationRequest(rqd));
        }

        @Test
        @DisplayName("Test when requestStatus differs from Pending throw exception")
        public void whenRequestStatusIsNotPendingThenThrowException() {
            rq = RecommendationRequest.builder()
                    .status(REQUEST_STATUS_REJECTED)
                    .build();
            assertThrows(DataValidationException.class, () ->
                    recommendationRequestValidator.validateRequestStatusNotAcceptedOrDeclined(rq));
        }
    }
}
