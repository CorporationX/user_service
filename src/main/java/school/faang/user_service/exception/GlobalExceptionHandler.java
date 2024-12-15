package school.faang.user_service.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SkillDuplicateException.class)
    public ResponseEntity<String> handleSkillDuplicateException(SkillDuplicateException ex) {
        log.error("SkillDuplicateException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SkillNotFoundException.class)
    public ResponseEntity<String> handleSkillNotFoundException(SkillNotFoundException exception) {
        log.error("SkillNotFoundException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exception) {
        log.error("UserNotFoundException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("IllegalArgumentException: {}", exception.getMessage(), exception);
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException exception) {
        log.error("IllegalStateException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException ex) {
        log.error("DataValidationException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RecommendationRequestNotFoundException.class)
    public ResponseEntity<String> handleRecommendationRequestNotFoundException(
            RecommendationRequestNotFoundException exception) {
        log.error("RecommendationRequestNotFoundException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("EntityNotFoundException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidMentorshipRequestException.class)
    public ResponseEntity<String> handleInvalidMentorshipRequestException(InvalidMentorshipRequestException ex) {
        log.error("InvalidMentorshipRequestException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidRequestFilterException.class)
    public ResponseEntity<String> handleInvalidRequestFilterException(InvalidRequestFilterException ex) {
        log.error("InvalidRequestFilterException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleAllExceptions(IOException exception) {
        log.error("IOException exception: {}", exception.getMessage(), exception);
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception exception) {
        log.error("Unhandled exception: {}", exception.getMessage(), exception);
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.error("ConstraintViolationException: {}", errorMessage, ex);
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<String> handlePaymentFailedException(PaymentFailedException e) {
        log.error("PaymentFailedException:", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(PremiumPeriodNotFoundException.class)
    public ResponseEntity<String> handlePremiumPeriodNotFoundException(PremiumPeriodNotFoundException e) {
        log.error("PremiumPeriodNotFountException:", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(SkillResourceNotFoundException.class)
    public ResponseEntity<String> handleInvalidMentorshipRequestException(SkillResourceNotFoundException ex) {
        log.error("SkillResourceNotFoundException: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handeAccessDeniedException(AccessDeniedException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(InvalidPreferredContactException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPreferredContact(InvalidPreferredContactException exception) {
        log.error("InvalidPreferredContactException occurred: {}", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .details("User attempted to set an invalid preferred contact method.")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @lombok.Builder
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private String message;
        private String details;
    }

}
