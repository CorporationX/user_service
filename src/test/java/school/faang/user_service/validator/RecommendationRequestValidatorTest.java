package school.faang.user_service.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.filter.RequestFilterDtoException;
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
    private RecommendationRequestDto rqd;
    private RecommendationRequestDto rqd2;
    private RecommendationRequest rq;
    private RequestFilterDto filterDto;
    private static final String VALID_MESSAGE = "Hi ho!";
    private static final long REQUESTER_ID_ONE = 1L;
    private static final long RECEIVER_ID_ONE = 1L;
    private static final long RECOMMENDATION_REQUEST_DTO_ID_ONE = 1L;
    private static final long RECOMMENDATION_REQUEST_ID_ONE = 1L;
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
        @DisplayName("When valid message passed doesn't throw exception")
        public void validMessageDoesNotThrowExceptionTest() {
            rqd = RecommendationRequestDto.builder()
                    .message(VALID_MESSAGE)
                    .build();
            assertDoesNotThrow(() -> recommendationRequestValidator.validateRecommendationRequestMessageNotNull(rqd));
        }

        @Test
        @DisplayName("When valid requester and receiver ids passed doesn't throw exception")
        public void requesterAndReceiverExistsPositiveTest() {
            rqd = RecommendationRequestDto.builder()
                    .requesterId(REQUESTER_ID_ONE)
                    .receiverId(RECEIVER_ID_ONE)
                    .build();
            when(recommendationRequestRepository.findById(rqd.getRequesterId()))
                    .thenReturn(Optional.of(RecommendationRequest.builder().build()));
            when(recommendationRequestRepository.findById(rqd.getReceiverId()))
                    .thenReturn(Optional.of(RecommendationRequest.builder().build()));

            assertDoesNotThrow(() -> recommendationRequestValidator.validateRequesterAndReceiverExists(rqd));
        }

        @Test
        @DisplayName("When valid rq id passed doesn't throw exception")
        public void validateRecommendationRequestExistsDoNotThrowExceptionTest() {
            rqd = RecommendationRequestDto.builder()
                    .id(RECOMMENDATION_REQUEST_DTO_ID_ONE)
                    .build();
            when(recommendationRequestRepository.findById(rqd.getId()))
                    .thenReturn(Optional.of(RecommendationRequest.builder().build()));

            assertDoesNotThrow(() -> recommendationRequestValidator.validateRecommendationRequestExists(rqd.getId()));
        }

        @Test
        @DisplayName("Validate request status is pending and doesn't throw exception")
        public void validateRequestStatusNotAcceptedOrDeclinedPositiveTest() {
            rq = RecommendationRequest.builder()
                    .id(RECOMMENDATION_REQUEST_ID_ONE)
                    .status(REQUEST_STATUS_PENDING)
                    .build();
            when(recommendationRequestRepository.findById(rq.getId())).thenReturn(Optional.of(rq));
            assertDoesNotThrow(() ->
                    recommendationRequestValidator.validateRequestStatusNotAcceptedOrDeclined(rq.getId()));
        }

        @Test
        @DisplayName("Validate previous request is valid and method doesn't throw exception")
        public void validatePreviousRequestDoNotThrowExceptionTest() {
            rqd = RecommendationRequestDto.builder()
                    .requesterId(REQUESTER_ID_ONE)
                    .receiverId(RECEIVER_ID_ONE)
                    .build();
            Optional<RecommendationRequest> optionalRecommendationRequest =
                    Optional.of(RecommendationRequest.builder().build());
            when(recommendationRequestRepository.findLatestPendingRequest(rqd.getRequesterId(), rqd.getReceiverId()))
                    .thenReturn(optionalRecommendationRequest);
            optionalRecommendationRequest.get().setCreatedAt(VALID_TIME);
            assertDoesNotThrow(() -> recommendationRequestValidator.validatePreviousRequest(rqd));
        }

        @Test
        @DisplayName("Validate if at least one field is not null doesn't throw exception")
        public void validateAtLeastOneFieldOfRequestFilterDtoNotNullDoesNotThrowExceptionTest() {
            filterDto = RequestFilterDto.builder()
                    .status(REQUEST_STATUS_PENDING)
                    .build();

            assertDoesNotThrow(() -> recommendationRequestValidator.validateRequestDtoFilterFieldsNotNull(filterDto));
        }
    }

    @Nested
    class NegativeTests {
        @Test
        @DisplayName("Check that exception is thrown in both null or empty message")
        public void nullOrEmptyRequestsMessageThrowsExceptionTest() {
            rqd = RecommendationRequestDto.builder()
                    .message(NULL_MESSAGE)
                    .build();
            rqd2 = RecommendationRequestDto.builder()
                    .message(EMPTY_MESSAGE)
                    .build();

            assertThrows(RecommendationRequestNotValidException.class, () -> recommendationRequestValidator
                    .validateRecommendationRequestMessageNotNull(rqd));
            assertThrows(RecommendationRequestNotValidException.class, () -> recommendationRequestValidator
                    .validateRecommendationRequestMessageNotNull(rqd2));
        }

        @Test
        @DisplayName("Test when receiver doesn't exist throw exception")
        public void receiverDoesNotExistsNegativeTest() {
            rqd = RecommendationRequestDto.builder()
                    .requesterId(REQUESTER_ID_ONE)
                    .receiverId(null)
                    .build();

            rq = RecommendationRequest.builder()
                    .build();

            when(recommendationRequestRepository.findById(rqd.getRequesterId()))
                    .thenReturn(Optional.of(rq));
            when(recommendationRequestRepository.findById(rqd.getReceiverId()))
                    .thenReturn(Optional.empty());

            assertThrows(RecommendationRequestNotValidException.class, () ->
                    recommendationRequestValidator.validateRequesterAndReceiverExists(rqd));
        }

        @Test
        @DisplayName("Test when requester doesn't exist throw exception")
        public void requesterDoesNotExistsNegativeTest() {
            rqd = RecommendationRequestDto.builder()
                    .requesterId(null)
                    .receiverId(RECEIVER_ID_ONE)
                    .build();

            rq = RecommendationRequest.builder()
                    .build();

            when(recommendationRequestRepository.findById(rqd.getRequesterId()))
                    .thenReturn(Optional.empty());
            when(recommendationRequestRepository.findById(rqd.getReceiverId()))
                    .thenReturn(Optional.of(rq));

            assertThrows(RecommendationRequestNotValidException.class, () ->
                    recommendationRequestValidator.validateRequesterAndReceiverExists(rqd));
        }

        @Test
        @DisplayName("Test when requester and receiver doesn't exist throw exception")
        public void requesterAndReceiverDoesNotExistsNegativeTest() {
            rqd = RecommendationRequestDto.builder()
                    .requesterId(null)
                    .receiverId(null)
                    .build();

            rq = RecommendationRequest.builder()
                    .build();

            when(recommendationRequestRepository.findById(rqd.getRequesterId()))
                    .thenReturn(Optional.empty());
            when(recommendationRequestRepository.findById(rqd.getReceiverId()))
                    .thenReturn(Optional.empty());

            assertThrows(RecommendationRequestNotValidException.class, () ->
                    recommendationRequestValidator.validateRequesterAndReceiverExists(rqd));
        }


        @Test
        @DisplayName("Validate that recommendationRequest doesn't exists throws exception")
        public void validateRecommendationRequestExistsThrowExceptionTest() {
            rqd = RecommendationRequestDto.builder()
                    .id(RECOMMENDATION_REQUEST_DTO_ID_ONE)
                    .build();

            when(recommendationRequestRepository.findById(rqd.getId())).thenReturn(Optional.empty());

            assertThrows(RecommendationRequestNotValidException.class, () ->
                    recommendationRequestValidator.validateRecommendationRequestExists(rqd.getId()));
        }

        @Test
        @DisplayName("Validate that from previous request hasn't passed 6 months throws exception")
        public void validatePreviousRequestThrowExceptionTest() {
            rqd = RecommendationRequestDto.builder()
                    .requesterId(REQUESTER_ID_ONE)
                    .receiverId(RECEIVER_ID_ONE)
                    .build();
            Optional<RecommendationRequest> optionalRecommendationRequest =
                    Optional.of(RecommendationRequest.builder().build());
            when(recommendationRequestRepository.findLatestPendingRequest(rqd.getRequesterId(), rqd.getReceiverId()))
                    .thenReturn(optionalRecommendationRequest);
            optionalRecommendationRequest.get().setCreatedAt(NOT_VALID_TIME);

            assertThrows(RecommendationRequestNotValidException.class, () ->
                    recommendationRequestValidator.validatePreviousRequest(rqd));
        }

        @Test
        @DisplayName("Test when requestStatus differs from Pending throw exception")
        public void validateRequestStatusNotAcceptedOrDeclinedThrowsException() {
            rq = RecommendationRequest.builder()
                    .id(REQUESTER_ID_ONE)
                    .status(REQUEST_STATUS_REJECTED)
                    .build();
            when(recommendationRequestRepository.findById(rq.getId())).thenReturn(Optional.of(rq));

            assertThrows(RecommendationRequestNotValidException.class, () ->
                    recommendationRequestValidator.validateRequestStatusNotAcceptedOrDeclined(rq.getId()));
        }

        @Test
        @DisplayName("Validate filterDto is null and throws exception")
        public void validateRequestDtoFilterIsNullThrowsExceptionTest() {
            filterDto = null;
            assertThrows(RequestFilterDtoException.class, () ->
                    recommendationRequestValidator.validateRequestDtoFilterFieldsNotNull(filterDto));
        }

        @Test
        @DisplayName("Validate filterDto fields list are empty throws exception")
        public void validateRequestFilterDtoFieldsAreNullThrowsExceptionTest() {
            filterDto = RequestFilterDto.builder()
                    .build();
            assertThrows(RequestFilterDtoException.class, () ->
                    recommendationRequestValidator.validateRequestDtoFilterFieldsNotNull(filterDto));
        }
    }
}
