package school.faang.user_service.exception;

import jakarta.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
  protected @NonNull ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      @NonNull HttpStatusCode status,
      WebRequest request) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    log.warn("Validation failed: {}", errors);

    return buildResponseEntity(HttpStatus.BAD_REQUEST, "Validation failed", errors);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      @NotNull HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    log.error("Malformed JSON request: {}", ex.getMessage());
    return buildResponseEntity(HttpStatus.BAD_REQUEST, "Malformed JSON request", null);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations()
        .forEach(
            violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));

    log.warn("Constraint violation: {}", errors);
    return buildResponseEntity(HttpStatus.BAD_REQUEST, "Constraint violation", errors);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
    log.warn("Access denied: {}", ex.getMessage());
    return buildResponseEntity(HttpStatus.FORBIDDEN, "Access is denied", null);
  }

  @ExceptionHandler(DataValidationException.class)
  public ResponseEntity<Object> handleDataValidationException(DataValidationException ex) {
    log.warn("Data validation error: {}", ex.getMessage());
    return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
    log.error("Entity not found: {}", ex.getMessage());
    return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage(), null);
  }

  @ExceptionHandler(Exception.class)
  public Object handleGlobalException(Exception ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", null);
  }

  private ResponseEntity<Object> buildResponseEntity(
      HttpStatus status, String message, Map<String, String> errors) {
    ApiError apiError = new ApiError(status, message, errors, LocalDateTime.now());
    return ResponseEntity.status(status).body(apiError);
  }
}
