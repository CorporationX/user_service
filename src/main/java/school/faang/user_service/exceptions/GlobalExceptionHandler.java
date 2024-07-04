package school.faang.user_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DuplicateMentorshipRequestException.class)
    public ResponseEntity<Map<String,String>> handleDuplicateMentorshipRequestException(DuplicateMentorshipRequestException ex) {
        return new ResponseEntity<>(Map.of("message",ex.getMessage(),"status",HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String,String>> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>(Map.of("message",ex.getMessage(),"status",HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
    }
}
