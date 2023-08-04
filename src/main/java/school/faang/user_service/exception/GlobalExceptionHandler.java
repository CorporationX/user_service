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
        return new ErrorResponse(request.getRequestURL().toString(), 404, "EntityNotFoundException", exception.getMessage());
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundExceptionPersistence(jakarta.persistence.EntityNotFoundException exception, HttpServletRequest request) {
        log.error("EntityNotFoundException: {}", exception.getMessage());
        return new ErrorResponse(request.getRequestURL().toString(), 404, "EntityNotFoundException", exception.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException exception, HttpServletRequest request) {
        log.error("DataValidationException: {}", exception.getMessage());
        return new ErrorResponse(request.getRequestURL().toString(), 400, "DataValidationException", exception.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleNullPointerException(NullPointerException exception, HttpServletRequest request) {
        log.error("NullPointerException: {}", exception.getMessage());
        return new ErrorResponse(request.getRequestURL().toString(), 500, "NullPointerException", exception.getMessage());
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

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(IllegalArgumentException exception, HttpServletRequest request) {
        log.error("IllegalArgumentException: {}", exception.getMessage());
        return new ErrorResponse(request.getRequestURL().toString(), 500, "IllegalArgumentException", exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException exception, HttpServletRequest request) {
        log.error("RuntimeException: {}", exception.getMessage());
        return new ErrorResponse(request.getRequestURL().toString(), 500, "RuntimeException", exception.getMessage());
    }
}
