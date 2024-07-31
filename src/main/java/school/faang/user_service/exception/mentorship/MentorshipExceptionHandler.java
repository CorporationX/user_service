package school.faang.user_service.exception.mentorship;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Evgenii Malkov
 */
@ControllerAdvice
public class MentorshipExceptionHandler {

    @ExceptionHandler(MentorshipNoSuchElementException.class)
    public ResponseEntity<String> handleNotFoundException(MentorshipNoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
