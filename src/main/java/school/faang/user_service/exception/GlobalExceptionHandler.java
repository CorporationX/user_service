package school.faang.user_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception, HttpServletRequest request) {
        log.error("EntityNotFoundException: {}", exception.getMessage());
        return new ErrorResponse(request.getRequestURL().toString(), HttpStatus.NOT_FOUND, "EntityNotFoundException", exception.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException exception, HttpServletRequest request) {
        log.error("DataValidationException: {}", exception.getMessage());
        return new ErrorResponse(request.getRequestURL().toString(), HttpStatus.BAD_REQUEST, "DataValidationException", exception.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleNullPointerException(NullPointerException exception, HttpServletRequest request) {
        log.error("NullPointerException: {}", exception.getMessage());
        return new ErrorResponse(request.getRequestURL().toString(), HttpStatus.INTERNAL_SERVER_ERROR, "NullPointerException", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException: {}", exception.getMessage());
        return exception.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException exception, HttpServletRequest request) {
        log.error("RuntimeException: {}", exception.getMessage());
        return new ErrorResponse(request.getRequestURL().toString(), HttpStatus.INTERNAL_SERVER_ERROR, "RuntimeException", exception.getMessage());
    }
}