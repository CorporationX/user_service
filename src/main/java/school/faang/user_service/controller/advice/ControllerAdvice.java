package school.faang.user_service.controller.advice;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.controller.goal.GoalInvitationController;
import school.faang.user_service.controller.user.UserController;

import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = {
        GoalInvitationController.class,
        UserController.class
})
public class ControllerAdvice {

    // Validator exceptions, based on convention chosen by the team
    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationNotPassedHandler(RuntimeException ex) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
    }

    // DTO (Jakarta) validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse dtoNotValidHandler(MethodArgumentNotValidException ex) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getBindingResult().getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(", "))
        );
    }

    // Internal exceptions, not caught by first two cases
    @ExceptionHandler({ Exception.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse genericExceptionHandler(Exception e) {
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage()
        );
    }
}
