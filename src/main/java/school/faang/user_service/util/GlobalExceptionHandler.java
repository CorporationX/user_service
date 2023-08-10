package school.faang.user_service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.exception.*;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity<Object> handleInvalidFieldException(InvalidFieldException ex) {
        log.error("Exception caused: {}. \n" +
                "Stacktrace: {}", ex.getMessage(), ex.getStackTrace());

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Exception caused: {}. \n" +
                "Stacktrace: {}", ex.getMessage(), ex.getStackTrace());

        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(TimingException.class)
    public ResponseEntity<Object> handleTimingException(TimingException ex) {
        log.error("Exception caused: {}. \n" +
                "Stacktrace: {}", ex.getMessage(), ex.getStackTrace());

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(SameEntityException.class)
    public ResponseEntity<Object> handleSameEntityException(SameEntityException ex) {
        log.error("Exception caused: {}. \n" +
                "Stacktrace: {}", ex.getMessage(), ex.getStackTrace());

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(EntityStateException.class)
    public ResponseEntity<Object> handleEntityStateException(EntityStateException ex) {
        log.error("Exception caused: {}. \n" +
                "Stacktrace: {}", ex.getMessage(), ex.getStackTrace());

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<Object> handleEntityAlreadyExistException(EntityAlreadyExistException ex) {
        log.error("Exception caused: {}. \n" +
                "Stacktrace: {}", ex.getMessage(), ex.getStackTrace());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> {
                    String fieldName = error.getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                    log.error("{} has an error: {}", fieldName, errorMessage);
                });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        log.error("Unknown exception caused: {}. \n" +
                "Stacktrace: {}", ex.getMessage(), ex.getStackTrace());
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}