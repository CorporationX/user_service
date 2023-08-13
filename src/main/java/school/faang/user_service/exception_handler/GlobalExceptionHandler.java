package school.faang.user_service.exception_handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.IncorrectIdException;
import school.faang.user_service.exception.UserNotFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error(exception.getMessage(), exception);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Object> handleDataValidationException(DataValidationException exception) {
        Map<String, String> body = Map.of("message", exception.getMessage());
        log.error(exception.getMessage(), exception);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({EntityNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(Exception exception) {
        Map<String, String> body = Map.of("message", exception.getMessage());
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(body);
    }

    @ExceptionHandler(IncorrectIdException.class)
    public ResponseEntity<Object> handleIncorrectIdException(IncorrectIdException exception) {
        Map<String, String> body = Map.of("message", exception.getMessage());
        log.error(exception.getMessage(), exception);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception exception) {
        Map<String, String> body = Map.of("message", exception.getMessage());
        log.error(exception.getMessage(), exception);
        return ResponseEntity.internalServerError().body(body);
    }
}
