package school.faang.user_service.controller.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.exception.promotion.PromotionValidationException;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PromotionControllerAdviceTest {
    private static final String TEST_MESSAGE = "test message";

    private final PromotionControllerExceptionHandler promotionControllerAdvice = new PromotionControllerExceptionHandler();

    @Test
    @DisplayName("Test promotion check exception handler")
    void testPromotionCheckExceptionHandlerSuccessful() {
        var promotionValidationException = new PromotionValidationException(TEST_MESSAGE);
        ResponseEntity<ProblemDetail> response = promotionControllerAdvice
                .handlePromotionValidationException(promotionValidationException);
        assertResponse(response, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Test promotion not found exception handler")
    void testPromotionNotFoundExceptionHandlerSuccessful() {
        var promotionNotFoundException = new PromotionNotFoundException(TEST_MESSAGE);
        ResponseEntity<ProblemDetail> response = promotionControllerAdvice
                .handlePromotionNotFoundException(promotionNotFoundException);
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