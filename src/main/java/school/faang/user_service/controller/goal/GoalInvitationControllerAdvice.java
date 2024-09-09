package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.dto.response.ErrorResponse;
import school.faang.user_service.exception.goal.invitation.InvitationEntityNotFoundException;
import school.faang.user_service.exception.goal.invitation.InvitationCheckException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GoalInvitationControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InvitationEntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(InvitationEntityNotFoundException exception) {
        log.error(exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvitationCheckException.class)
    public ErrorResponse handleInvitationCheckException(InvitationCheckException exception) {
        log.error(exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }
}
