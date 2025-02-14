package school.faang.user_service.exception.globalexceptionhandler;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS_MAP = Map.of(
            MethodArgumentTypeMismatchException.class, HttpStatus.BAD_REQUEST,
            MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST,
            HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST,
            IllegalArgumentException.class, HttpStatus.BAD_REQUEST,
            NoSuchElementException.class, HttpStatus.NOT_FOUND,
            EntityNotFoundException.class, HttpStatus.NOT_FOUND
    );

    private static final Map<Class<? extends Exception>, String> EXCEPTION_DEFAULT_MESSAGE_MAP = Map.of(
            HttpMessageNotReadableException.class, "Cannot parse JSON data. Please check it again.",
            MethodArgumentTypeMismatchException.class, "Invalid id type. Please provide the correct type.");

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        String message = EXCEPTION_DEFAULT_MESSAGE_MAP.getOrDefault(ex.getClass(),
                EXCEPTION_STATUS_MAP.containsKey(ex.getClass()) ? ex.getMessage() : "An unexpected error occurred.");

        log.error("Exception caught: {}", ex.getClass().getSimpleName(), ex);
        if (ex instanceof MethodArgumentNotValidException validationEx) {
            return ResponseEntity.status(status).body(buildValidationExMap(validationEx));
        }
        return ResponseEntity.status(status).body(message);
    }

    private Map<String, String> buildValidationExMap(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(err -> errors.put(
                        ((FieldError) err).getField(),
                        err.getDefaultMessage()));
        return errors;
    }
}
