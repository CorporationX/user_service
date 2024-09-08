package school.faang.user_service.controller.advice;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.controller.goal.GoalInvitationController;
import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = {
        GoalInvitationController.class}
)
public class ApiControllerAdvice {

    // Validator exceptions, based on convention chosen by the team
    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponse> validationNotPassedHandler(RuntimeException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // DTO (Jakarta) validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> dtoNotValidHandler(MethodArgumentNotValidException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getBindingResult().getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(", "))
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Internal exceptions, not caught by first two cases
    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ErrorResponse> genericExceptionHandler() {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Something wrong on our end, contact IT support immediately."
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
