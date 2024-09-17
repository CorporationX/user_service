package school.faang.user_service.controller.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.exception.premium.PremiumValidationFailureException;
import school.faang.user_service.exception.premium.PremiumNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PremiumControllerExceptionHandlerTest {
    private static final String TEST_MESSAGE = "test message";

    @InjectMocks
    private PremiumControllerExceptionHandler premiumControllerExceptionHandler;

    @Test
    @DisplayName("Test premium check failure exception handler")
    void testPremiumCheckFailureExceptionSuccessful() {
        var premiumValidationFailureException = new PremiumValidationFailureException(TEST_MESSAGE);
        ResponseEntity<ProblemDetail> response = premiumControllerExceptionHandler
                .handlePremiumValidationFailureException(premiumValidationFailureException);
        assertResponse(response, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Test premium not found exception handler")
    void testPremiumNotFoundExceptionHandlerSuccessful() {
        var premiumNotFoundException = new PremiumNotFoundException(TEST_MESSAGE);
        ResponseEntity<ProblemDetail> response = premiumControllerExceptionHandler
                .premiumNotFoundExceptionHandler(premiumNotFoundException);
        assertResponse(response, HttpStatus.NOT_FOUND);
    }

    private void assertResponse(ResponseEntity<ProblemDetail> response, HttpStatus httpStatus) {
        assertThat(response)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(httpStatus);
        assertThat(response.getBody())
                .isNotNull()
                .extracting(ProblemDetail::getStatus)
                .isEqualTo(httpStatus.value());
        assertThat(response.getBody())
                .extracting(ProblemDetail::getDetail)
                .isEqualTo(TEST_MESSAGE);
    }
}