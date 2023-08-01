package school.faang.user_service.util.exceptionhandler.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.NotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler1 {
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        System.out.println(ex.getClass() + " exception");
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        System.out.println(ex.getClass() + " exception");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
