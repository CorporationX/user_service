package school.faang.user_service.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdvice {
    private final Map<Class<? extends Exception>, HttpStatus> exceptionStatus = Map.of(
            EntityNotFoundException.class, HttpStatus.NOT_FOUND,
            MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST,
            IllegalArgumentException.class, HttpStatus.BAD_REQUEST,
            IllegalStateException.class, HttpStatus.BAD_REQUEST,
            SecurityException.class, HttpStatus.UNAUTHORIZED
    );

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> businessLogicHandler(Exception e) {
        HttpStatus status = exceptionStatus.getOrDefault(e.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        String message = e instanceof MethodArgumentNotValidException ex ?
                ex.getBindingResult().getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(", "))
                : e.getMessage();
        ErrorResponse response = ErrorResponse.of(status, message);
        return new ResponseEntity<>(response, status);
    }
}
