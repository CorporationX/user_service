package school.faang.user_service.exception;

import jakarta.validation.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected @NonNull ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            @NonNull HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.warn("Validation failed for request: {}", errors);

        ApiError apiError = createApiError(HttpStatus.BAD_REQUEST, "Validation failed", errors);
        return ResponseEntity.badRequest().body(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        if (ex instanceof BindException bindException) {
            Map<String, String> errors = new HashMap<>();
            bindException.getBindingResult().getFieldErrors()
                    .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

            ApiError apiError = createApiError((HttpStatus) status, "Binding failed", errors);
            return ResponseEntity.status(status).headers(headers).body(apiError);
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, @NotNull HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        logger.error("Malformed JSON request: {}", ex.getMessage());
        ApiError apiError = createApiError(HttpStatus.BAD_REQUEST, "Malformed JSON request", null);
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.warn("Constraint violation: {}", errors);
        return ResponseEntity.badRequest().body(createApiError(HttpStatus.BAD_REQUEST, "Constraint violation", errors));
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ApiError> handleDataValidationException(DataValidationException ex) {
        logger.warn("Data validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(createApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), null));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.error("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createApiError(HttpStatus.NOT_FOUND, ex.getMessage(), null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createApiError(HttpStatus.FORBIDDEN, "Access is denied", null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(createApiError(HttpStatus.BAD_REQUEST, "Invalid argument", null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException ex) {
        logger.error("Unexpected runtime error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Runtime error occurred", null));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception ex) {
        logger.error("Unexpected global error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", null));
    }
    private ApiError createApiError(HttpStatus status, String message, Map<String, String> errors) {
        return new ApiError(status, message, errors, LocalDateTime.now());
    }
}