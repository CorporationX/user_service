package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import school.faang.user_service.dto.ErrorResponse;
import school.faang.user_service.exception.goal.UserReachedMaxGoalsException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionApiHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityFieldsException.class)
    public ResponseEntity<ErrorResponse> handleEntityFieldsException(EntityFieldsException e) {
        ErrorResponse response = getErrorResponse(e, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        ErrorResponse response = getErrorResponse(e, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @ExceptionHandler(EntityUpdateException.class)
    public ResponseEntity<ErrorResponse> handleEntityUpdateException(EntityUpdateException e) {
        ErrorResponse response = getErrorResponse(e, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @ExceptionHandler(UserReachedMaxGoalsException.class)
    public ResponseEntity<ErrorResponse> handleUserReachedMaxGoalsException(UserReachedMaxGoalsException e) {
        ErrorResponse response = getErrorResponse(e, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    private ErrorResponse getErrorResponse(Exception e, HttpStatus status) {
        return ErrorResponse.builder()
                .dateTime(LocalDateTime.now())
                .exception(e.getClass().getSimpleName())
                .message(e.getMessage())
                .status(status.value())
                .build();
    }
}
