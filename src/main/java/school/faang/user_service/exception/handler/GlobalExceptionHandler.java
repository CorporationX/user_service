package school.faang.user_service.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import school.faang.user_service.dto.DtoValidationConstraints;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse errorResponse = new ErrorResponse(DtoValidationConstraints.VALIDATION_FAILED, details);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(Exception ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("Entity not found at {}. Details: {}", uri, ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), uri);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(Exception ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("Illegal state detected at {}. Details: {}", uri, ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), uri);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(Exception ex, WebRequest request) {
        String uri = request.getDescription(false).replace("uri=", "");
        log.error("Incorrect data passed at {}. Details: {}", uri, ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), uri);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("An unexpected exception occurred. Details: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
