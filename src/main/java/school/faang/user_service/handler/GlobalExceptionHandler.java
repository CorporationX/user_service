package school.faang.user_service.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.exception.ErrorResponse;
import school.faang.user_service.exception.UserAlreadyRegisteredAtEvent;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.UserNotRegisteredAtEvent;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidException(DataValidException dve) {
        return new ErrorResponse(dve.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException manve) {
        return manve.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), ""
                        ))
                );
    }

    @ExceptionHandler(UserAlreadyRegisteredAtEvent.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserAlreadyRegisteredAtEvent(UserAlreadyRegisteredAtEvent uarae){
        return new ErrorResponse(uarae.getMessage());
    }

    @ExceptionHandler(UserNotRegisteredAtEvent.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotRegisteredAtEvent(UserNotRegisteredAtEvent unrae){
        return new ErrorResponse(unrae.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException exception, HttpServletRequest request) {
        log.error("Cannot find particular user", exception.getMessage(), exception);
        return buildErrorResponse(exception, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException re){
        return new ErrorResponse(re.getMessage());
    }

    private ErrorResponse buildErrorResponse(UserNotFoundException exception, HttpServletRequest request, HttpStatus status) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(request.getRequestURI())
                .status(status)
                .message(exception.getMessage())
                .build();
    }
}
