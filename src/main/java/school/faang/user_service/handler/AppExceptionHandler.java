package school.faang.user_service.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import school.faang.user_service.handler.exception.EntityExistException;
import school.faang.user_service.handler.exception.ExceptionMessage;
import school.faang.user_service.handler.exception.ManyExceptionMessage;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AppExceptionHandler {
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ExceptionMessage exceptionMessage = new ExceptionMessage(
                LocalDateTime.now(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(exceptionMessage, httpStatus);
    }

    @ExceptionHandler(EntityExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleEntityExistException(EntityExistException e, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ExceptionMessage exceptionMessage = new ExceptionMessage(
                LocalDateTime.now(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(exceptionMessage, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ManyExceptionMessage exceptionMessage = new ManyExceptionMessage(
                LocalDateTime.now(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                e.getAllErrors().stream().collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), ""
                        ))
                ),
                request.getRequestURI()
        );
        return new ResponseEntity<>(exceptionMessage, httpStatus);
    }
}
