package school.faang.user_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleValidationException() {
        DataValidationException exception = new DataValidationException("Invalid data provided");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data provided", response.getBody().get("message"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void testHandlePaymentFailedException() {
        PaymentFailedException exception = new PaymentFailedException("Payment failed due to insufficient funds");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handlePaymentFailedException(exception);

        assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
        assertEquals("Payment failed due to insufficient funds", response.getBody().get("message"));
        assertEquals(402, response.getBody().get("status"));
    }

    @Test
    void testHandleAllExceptions() {
        Exception exception = new Exception("Unexpected error");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAllExceptions(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred.", response.getBody().get("message"));
        assertEquals(500, response.getBody().get("status"));
    }
}