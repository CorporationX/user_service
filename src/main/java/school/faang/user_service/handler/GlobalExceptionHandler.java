package school.faang.user_service.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DataValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ErrorResponse handleDataValidationException(RuntimeException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    private ErrorResponse buildErrorResponse(Exception ex, int status, String message) {
        return ErrorResponse.create(ex, HttpStatusCode.valueOf(status), message);
    }
}
