package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecommendationRequestValidatorTest {

    private RecommendationRequestValidator recommendationRequestValidator;

    @BeforeEach
    void setUp() {
        recommendationRequestValidator = new RecommendationRequestValidator();
    }

    // Tests for validateRequesterAndReceiver method
    @Test
    void validateRequesterAndReceiver_ShouldThrowException_WhenRequesterAndReceiverAreSame() {
        Long requesterId = 1L;
        Long receiverId = 1L;

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                recommendationRequestValidator.validateRequesterAndReceiver(requesterId, receiverId)
        );

        assertEquals("Requester and receiver cannot be the same person.", exception.getMessage());
    }

    @Test
    void validateRequesterAndReceiver_ShouldNotThrowException_WhenRequesterAndReceiverAreDifferent() {
        Long requesterId = 1L;
        Long receiverId = 2L;

        assertDoesNotThrow(() -> recommendationRequestValidator.validateRequesterAndReceiver(requesterId, receiverId));
    }

    // Tests for validateRequestAndCheckTimeLimit method
    @Test
    void validateRequestAndCheckTimeLimit_ShouldThrowException_WhenTimeLimitHasNotPassed() {
        LocalDateTime lastRequestTime = LocalDateTime.now().minusMonths(3);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                recommendationRequestValidator.validateRequestAndCheckTimeLimit(lastRequestTime)
        );

        assertEquals("Not enough time has passed since the last request.", exception.getMessage());
    }

    @Test
    void validateRequestAndCheckTimeLimit_ShouldNotThrowException_WhenTimeLimitHasPassed() {
        LocalDateTime lastRequestTime = LocalDateTime.now().minusMonths(7);

        assertDoesNotThrow(() -> recommendationRequestValidator.validateRequestAndCheckTimeLimit(lastRequestTime));
    }

    @Test
    void validateRequestAndCheckTimeLimit_ShouldNotThrowException_WhenLastRequestTimeIsNull() {
        assertDoesNotThrow(() -> recommendationRequestValidator.validateRequestAndCheckTimeLimit(null));
    }
}
