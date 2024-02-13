package school.faang.user_service.handlers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(httpStatus)
                .body(new ErrorResponse(httpStatus.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(httpStatus)
                .body(new ErrorResponse(httpStatus.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDataValidationException(ValidationException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(httpStatus)
                .body(new ErrorResponse(httpStatus.value(), ex.getMessage()));
    }
}