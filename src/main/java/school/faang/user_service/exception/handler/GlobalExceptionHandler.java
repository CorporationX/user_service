package school.faang.user_service.exception.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DuplicateObjectException;
import school.faang.user_service.exception.GoalInvitationStatusException;
import school.faang.user_service.exception.GoalInvitationValidationException;
import school.faang.user_service.exception.ValueExceededException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

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
