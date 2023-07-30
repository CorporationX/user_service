package school.faang.user_service.exceptionHandler.mentorshipRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.util.mentorshipRequest.exception.GetRequestsMentorshipsException;
import school.faang.user_service.util.mentorshipRequest.exception.IncorrectIdException;
import school.faang.user_service.util.mentorshipRequest.exception.NoRequestsException;
import school.faang.user_service.util.mentorshipRequest.exception.RequestIsAlreadyAcceptedException;
import school.faang.user_service.util.mentorshipRequest.exception.RequestIsAlreadyRejectedException;
import school.faang.user_service.util.mentorshipRequest.exception.RequestMentorshipException;
import school.faang.user_service.util.mentorshipRequest.exception.SameMentorAndMenteeException;
import school.faang.user_service.util.mentorshipRequest.exception.TimeHasNotPassedException;
import school.faang.user_service.util.mentorshipRequest.exception.UnknownRejectionReasonException;
import school.faang.user_service.util.mentorshipRequest.exception.UserNotFoundException;
import school.faang.user_service.util.mentorshipRequest.response.ErrorResponse;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(UserNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Incorrect inputs about users",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SameMentorAndMenteeException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(SameMentorAndMenteeException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "The same mentor is specified",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestMentorshipException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(RequestMentorshipException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TimeHasNotPassedException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(TimeHasNotPassedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "The request can be sent once every three months",
                System.currentTimeMillis()
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

        return new ResponseEntity<>(new ErrorResponse(message.toString(),
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(NoSuchElementException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Some elements are not in a database",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GetRequestsMentorshipsException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(GetRequestsMentorshipsException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoRequestsException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(NoRequestsException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "No requests were created",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestIsAlreadyAcceptedException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(RequestIsAlreadyAcceptedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "This request is already accepted",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestIsAlreadyRejectedException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(RequestIsAlreadyRejectedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "This request is already rejected",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectIdException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(IncorrectIdException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Id can't be lower than 1",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownRejectionReasonException.class)
    private ResponseEntity<ErrorResponse> handleExceptions(UnknownRejectionReasonException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Rejection's reason must be specified",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
