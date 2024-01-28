package school.faang.user_service.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.exception.PaymentProcessingException;
import school.faang.user_service.exception.UserNotFoundException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DataValidationException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(Exception exception, HttpServletRequest request) {
        log.error("Error: {}", exception);
        return getErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class, EventNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(Exception exception, HttpServletRequest request) {
        log.error("Error: {}", exception);
        return getErrorResponse(request.getRequestURI(), HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException exception, HttpServletRequest request) {
        log.error("Error: {}", exception);
        return getErrorResponse(request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(PaymentProcessingException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleRuntimeException(PaymentProcessingException exception, HttpServletRequest request) {
        log.error("Error: {}", exception);
        return getErrorResponse(request.getRequestURI(), HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }

    private ErrorResponse getErrorResponse(String url, HttpStatus status, String message) {
        return ErrorResponse
                .builder()
                .timestamp(LocalDateTime.now())
                .url(url)
                .status(status)
                .message(message)
                .build();
    }
}