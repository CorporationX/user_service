package school.faang.user_service.exception.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import school.faang.user_service.exception.SkillAssignmentException;

@Slf4j
@RestControllerAdvice
public class GlobalRestExceptionHandler {
    private static final String BAD_REQUEST_MESSAGE = "Sorry, you've provided an invalid request." +
            " Please check the request format and ensure all required parameters are correct.";

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(BAD_REQUEST_MESSAGE, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SkillAssignmentException.class)
    public ResponseEntity<String> handleSkillAssignmentException(SkillAssignmentException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
