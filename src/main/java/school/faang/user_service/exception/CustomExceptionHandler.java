package school.faang.user_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException exception, HttpServletRequest request) {
        log.error("Cannot find particular user", exception.getMessage(), exception);
        return buildErrorResponse(exception, request, HttpStatus.NOT_FOUND);
    }

    private ErrorResponse buildErrorResponse(UserNotFoundException exception, HttpServletRequest request, HttpStatus status) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(request.getRequestURI())
                .status(status)
                .message(exception.getMessage())
                .build();

        return errorResponse;
    }
}