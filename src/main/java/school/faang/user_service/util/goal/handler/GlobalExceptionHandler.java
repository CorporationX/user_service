package school.faang.user_service.util.goal.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.dto.goal.ErrorResponse;
import school.faang.user_service.util.goal.exception.AcceptingGoalInvitationException;
import school.faang.user_service.util.goal.exception.CreateInvitationException;
import school.faang.user_service.util.goal.exception.GoalInvitationNotFoundException;
import school.faang.user_service.util.goal.exception.GoalNotFoundException;
import school.faang.user_service.util.goal.exception.IncorrectIdException;
import school.faang.user_service.util.goal.exception.IncorrectStatusException;
import school.faang.user_service.util.goal.exception.MappingGoalInvitationDtoException;
import school.faang.user_service.util.goal.exception.RejectionGoalInvitationException;
import school.faang.user_service.util.goal.exception.UseFiltersException;
import school.faang.user_service.util.goal.exception.UserNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CreateInvitationException.class)
    public ResponseEntity<ErrorResponse> handle(CreateInvitationException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(MappingGoalInvitationDtoException.class)
    public ResponseEntity<ErrorResponse> handle(MappingGoalInvitationDtoException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(UserNotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder message = new StringBuilder();

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(
                    error -> message.append(error.getDefaultMessage())
            );
        }

        return new ResponseEntity<>(new ErrorResponse(message.toString(),
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GoalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(GoalNotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(IncorrectIdException.class)
    public ResponseEntity<ErrorResponse> handle(IncorrectIdException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(GoalInvitationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(GoalInvitationNotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(AcceptingGoalInvitationException.class)
    public ResponseEntity<ErrorResponse> handle(AcceptingGoalInvitationException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(RejectionGoalInvitationException.class)
    public ResponseEntity<ErrorResponse> handle(RejectionGoalInvitationException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(IncorrectStatusException.class)
    public ResponseEntity<ErrorResponse> handle(IncorrectStatusException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(UseFiltersException.class)
    public ResponseEntity<ErrorResponse> handle(UseFiltersException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }
}
