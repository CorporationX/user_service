package school.faang.user_service.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ParticipantRegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleParticipantRegistration(ParticipantRegistrationException exception){
        log.error("Participant Registration Error: {}", exception.getMessage());
        return new ErrorResponse("Participant Registration Error: {}", exception.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Произошло исключение IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException ex) {
        log.error("Ошибка доступа к базе данных: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка базы данных.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Произошла ошибка: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка.");
    }

    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<String> handleInvalidUserIdException(InvalidUserIdException ex) {
        log.warn("Произошло исключение InvalidUserIdException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(SubscriptionAlreadyExistsException.class)
    public ResponseEntity<String> handleSubscriptionAlreadyExistsException(SubscriptionAlreadyExistsException ex) {
        log.warn("Произошло исключение SubscriptionAlreadyExistsException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ResponseEntity<String> handleSubscriptionNotFoundException(SubscriptionNotFoundException ex) {
        log.warn("Произошло исключение SubscriptionNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(GoalInvitationValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleGoalInvitationValidation(GoalInvitationValidationException e) {
        log.error("Goal Invitation Validation Error: {}", e.getMessage(), e);
        return new ErrorResponse("Goal Invitation Validation Error", e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFound(EntityNotFoundException e) {
        log.error("Entity Not Found: {}", e.getMessage(), e);
        return new ErrorResponse("Entity Not Found", e.getMessage());
    }

    @ExceptionHandler(DuplicateObjectException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDuplicateObject(DuplicateObjectException e) {
        log.error("Duplicated object: {}", e.getMessage(), e);
        return new ErrorResponse("Duplicated object", e.getMessage());
    }

    @ExceptionHandler(GoalInvitationStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleGoalInvitationStatus(GoalInvitationStatusException e) {
        log.error("Goal Invitation Status Error: {}", e.getMessage(), e);
        return new ErrorResponse("Goal Invitation Status Error", e.getMessage());
    }

    @ExceptionHandler(ValueExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValueExceeded(ValueExceededException e) {
        log.error("Value Exceeded: {}", e.getMessage(), e);
        return new ErrorResponse("Value Exceeded", e.getMessage());
    }
}
