package school.faang.user_service.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    public void handleMethodArgumentNotValidTest() {

    }

    @Test
    public void handleDataValidationTest() {
        DataValidationException dataValidationException = new DataValidationException("Ошибка");

        ErrorResponse errorResponse = globalExceptionHandler.handleDataValidation(dataValidationException);

        assertEquals("Ошибка", errorResponse.getError());
        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getHttpStatus());
    }

    @Test
    public void handleEntityNotFoundTest() {
        EntityNotFoundException entityNotFoundException = new EntityNotFoundException("Ошибка");

        ErrorResponse errorResponse = globalExceptionHandler.handleEntityNotFound(entityNotFoundException);

        assertEquals("Ошибка", errorResponse.getError());
        assertEquals(HttpStatus.NOT_FOUND, errorResponse.getHttpStatus());
    }

    @Test
    public void handleRuntimeTest() {
        RuntimeException runtimeException = new RuntimeException("Ошибка");

        ErrorResponse errorResponse = globalExceptionHandler.handleRuntime(runtimeException);

        assertEquals("Ошибка", errorResponse.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getHttpStatus());
    }
}