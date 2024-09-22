package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MentorshipExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ExceptionResponse> userNotFoundHandle(Exception e) {
        return new ResponseEntity<>(
                new ExceptionResponse(
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage()
                ),
                HttpStatus.NOT_FOUND);
    }
}
