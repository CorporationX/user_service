package school.faang.user_service.service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.GlobalExceptionHandler;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleDataValidationException() {
        DataValidationException exception = new DataValidationException("Validation failed");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleDataValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Error", Objects.requireNonNull(response.getBody()).get("error"));
        assertEquals("Validation failed", response.getBody().get("message"));
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid input");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Argument", Objects.requireNonNull(response.getBody()).get("error"));
        assertEquals("Invalid input", response.getBody().get("message"));
    }

    @Test
    void testHandleNoSuchElementException() {
        NoSuchElementException exception = new NoSuchElementException("Element not found");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleNoSuchElementException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not Found", Objects.requireNonNull(response.getBody()).get("error"));
        assertEquals("Element not found", response.getBody().get("message"));
    }

    @Test
    void testHandleIllegalStateException() {
        IllegalStateException exception = new IllegalStateException("Invalid state");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleIllegalStateException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Invalid State", Objects.requireNonNull(response.getBody()).get("error"));
        assertEquals("Invalid state", response.getBody().get("message"));
    }

    @Test
    void testHandleAllExceptions() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleAllExceptions(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", Objects.requireNonNull(response.getBody()).get("error"));
        assertEquals("Unexpected error", response.getBody().get("message"));
    }
}