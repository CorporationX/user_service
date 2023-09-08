package school.faang.user_service.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import school.faang.user_service.exception.*;

import java.time.LocalDateTime;


@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<Object> handleRequestValidationException(RequestValidationException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.BAD_REQUEST, "Invalid Request");
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<Object> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException e, HttpServletRequest rq) {
        return buildErrorResponse(e, rq, HttpStatus.CONFLICT, "Already exists");
    }

    @ExceptionHandler(GoalValidationException.class)
    public ResponseEntity<Object> handleGoalValidationException(GoalValidationException e, HttpServletRequest request) {
        return buildErrorResponse(e, request, HttpStatus.BAD_REQUEST, "Goal Validation Error");
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleFileUploadException(FileUploadException e, HttpServletRequest request) {
        return buildErrorResponse(e, request, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private ResponseEntity<Object> buildErrorResponse(Exception e, HttpServletRequest rq, HttpStatus status, String error) {
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .url(rq.getRequestURI())
            .status(status.value())
            .error(error)
            .message(e.getMessage())
            .build();

        return new ResponseEntity<>(errorResponse, status);
    }
}