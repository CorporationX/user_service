package school.faang.user_service.util.exceptionhandler.v4;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.dto.response.ErrorResponse;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.AcceptingGoalInvitationException;
import school.faang.user_service.exception.CreateInvitationException;
import school.faang.user_service.exception.GoalInvitationNotFoundException;
import school.faang.user_service.exception.GoalNotFoundException;
import school.faang.user_service.exception.IncorrectIdException;
import school.faang.user_service.exception.IncorrectStatusException;
import school.faang.user_service.exception.MappingGoalInvitationDtoException;
import school.faang.user_service.exception.RejectionGoalInvitationException;
import school.faang.user_service.exception.UseFiltersException;

@ControllerAdvice
public class GlobalExceptionHandler4 {

    @ExceptionHandler(CreateInvitationException.class)
    public ResponseEntity<ErrorResponse> handle(CreateInvitationException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(MappingGoalInvitationDtoException.class)
    public ResponseEntity<ErrorResponse> handle(MappingGoalInvitationDtoException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(UserNotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
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

        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                message.toString()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GoalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(GoalNotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage()));
    }

    @ExceptionHandler(IncorrectIdException.class)
    public ResponseEntity<ErrorResponse> handle(IncorrectIdException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage()));
    }

    @ExceptionHandler(GoalInvitationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(GoalInvitationNotFoundException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage()));
    }

    @ExceptionHandler(AcceptingGoalInvitationException.class)
    public ResponseEntity<ErrorResponse> handle(AcceptingGoalInvitationException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage()));
    }

    @ExceptionHandler(RejectionGoalInvitationException.class)
    public ResponseEntity<ErrorResponse> handle(RejectionGoalInvitationException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage()));
    }

    @ExceptionHandler(IncorrectStatusException.class)
    public ResponseEntity<ErrorResponse> handle(IncorrectStatusException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage()));
    }

    @ExceptionHandler(UseFiltersException.class)
    public ResponseEntity<ErrorResponse> handle(UseFiltersException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage()));
    }
}
