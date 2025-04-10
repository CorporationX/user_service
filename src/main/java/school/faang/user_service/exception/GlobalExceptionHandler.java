package school.faang.user_service.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(
                        error -> {
                            String fieldName = ((FieldError) error).getField();
                            String errorMessage = error.getDefaultMessage();
                            errors.put(fieldName, errorMessage);
                        });

        log.error("Validation failed: {}", errors);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        log.error("Malformed JSON request: {}", ex.getMessage());
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, "Malformed JSON request");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(
                        violation ->
                                errors.put(
                                        violation.getPropertyPath().toString(),
                                        violation.getMessage()));

        log.error("Constraint violation: {}", errors);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, "Constraint violation", errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage(), ex);
        return buildErrorResponseEntity(HttpStatus.FORBIDDEN, "Access is denied");
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        String message = ex.getMessage();
        log.error("Bad request: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Object> handleDataValidationException(DataValidationException ex) {
        String message = ex.getMessage();
        log.error("Data validation error: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        String message = ex.getMessage();
        log.error("Entity not found: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.NOT_FOUND, message);
    }

    @ExceptionHandler(EventSerializationException.class)
    public ResponseEntity<Object> handleEventSerializationException(
            EventSerializationException ex) {
        String message = ex.getMessage();
        log.error("Event serialization: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    @ExceptionHandler(RecommendationAlreadyGivenException.class)
    public ResponseEntity<Object> handleRecommendationAlreadyGivenException(
            RecommendationAlreadyGivenException ex) {
        String message = ex.getMessage();
        log.error("Recommendation already given: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(SkillNotFoundException.class)
    public ResponseEntity<Object> handleSkillNotFoundException(SkillNotFoundException ex) {
        String message = ex.getMessage();
        log.error("Skill not found: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return buildErrorResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<Object> buildErrorResponseEntity(HttpStatus status, String message) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message);
        return ResponseEntity.status(status).body(errorResponse);
    }

    private ResponseEntity<Object> buildErrorResponseEntity(
            HttpStatus status, String message, Map<String, String> errors) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message, errors);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
