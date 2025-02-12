package school.faang.user_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import java.util.NoSuchElementException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final Map<Class<? extends Exception>, ErrorMessages> ERROR_STATUS_MAP = new HashMap<>();

    static {
        ERROR_STATUS_MAP.put(DataValidationException.class, ErrorMessages.BAD_REQUEST);
        ERROR_STATUS_MAP.put(BusinessException.class, ErrorMessages.UNPROCESSABLE_ENTITY);
        ERROR_STATUS_MAP.put(DiceBearException.class, ErrorMessages.INTERNAL_SERVER_ERROR);
        ERROR_STATUS_MAP.put(S3Exception.class, ErrorMessages.INTERNAL_SERVER_ERROR);

        ERROR_STATUS_MAP.put(NoSuchElementException.class, ErrorMessages.NOT_FOUND);
        ERROR_STATUS_MAP.put(IllegalStateException.class, ErrorMessages.CONFLICT);
        ERROR_STATUS_MAP.put(IllegalArgumentException.class, ErrorMessages.BAD_REQUEST);

        ERROR_STATUS_MAP.put(Exception.class, ErrorMessages.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation exception: {}", ex.getMessage(), ex);

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        ErrorMessages errorMessage = ERROR_STATUS_MAP.getOrDefault(ex.getClass(), ErrorMessages.INTERNAL_SERVER_ERROR);

        log.error("Exception caught: [{}] - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        Map<String, String> response = new HashMap<>();
        response.put("error", errorMessage.getMessage());
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, errorMessage.getStatus());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
