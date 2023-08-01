package school.faang.user_service.util.exceptionhandler.v2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.dto.response.ErrorResponse;
import school.faang.user_service.util.exception.GetRequestsMentorshipsException;
import school.faang.user_service.util.exception.IncorrectIdException;
import school.faang.user_service.util.exception.NoRequestsException;
import school.faang.user_service.util.exception.RequestIsAlreadyAcceptedException;
import school.faang.user_service.util.exception.RequestIsAlreadyRejectedException;
import school.faang.user_service.util.exception.RequestMentorshipException;
import school.faang.user_service.util.exception.SameMentorAndMenteeException;
import school.faang.user_service.util.exception.TimeHasNotPassedException;
import school.faang.user_service.util.exception.UnknownRejectionReasonException;
import school.faang.user_service.util.exception.UserNotFoundException;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(UserNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Incorrect inputs about users"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SameMentorAndMenteeException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(SameMentorAndMenteeException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "The same mentor is specified"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestMentorshipException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(RequestMentorshipException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TimeHasNotPassedException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(TimeHasNotPassedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "The request can be sent once every three months"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(NoSuchElementException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(NoSuchElementException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Some elements are not in a database"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GetRequestsMentorshipsException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(GetRequestsMentorshipsException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoRequestsException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(NoRequestsException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "No requests were created"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestIsAlreadyAcceptedException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(RequestIsAlreadyAcceptedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "This request is already accepted"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestIsAlreadyRejectedException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(RequestIsAlreadyRejectedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "This request is already rejected"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectIdException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(IncorrectIdException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Id can't be lower than 1"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownRejectionReasonException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(UnknownRejectionReasonException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Rejection's reason must be specified"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
