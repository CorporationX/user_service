package school.faang.user_service.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String LOG_MESSAGE_EXCEPTION = "In class: {}, message: {}, error: {}, time: {}";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        loggingInfo(e);
        return e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDto handleDataValidationException(DataValidationException e) {
        loggingInfo(e);
        return constructorForErrorMessageDto(e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDto handleEntityNotFoundException(EntityNotFoundException e) {
        loggingInfo(e);
        return constructorForErrorMessageDto(e);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageDto handRuntimeException(RuntimeException e) {
        loggingError(e);
        return constructorForErrorMessageDto(e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessageDto handException(Exception e) {
        loggingError(e);
        return constructorForErrorMessageDto(e);
    }

    private void loggingError(Exception e) {
        log.error(LOG_MESSAGE_EXCEPTION, e.getClass(), e.getMessage(), e.getCause(), LocalDateTime.now());
    }

    private void loggingInfo(Exception e) {
        log.info(LOG_MESSAGE_EXCEPTION, e.getClass(), e.getMessage(), e.getCause(), LocalDateTime.now());
    }

    private ErrorMessageDto constructorForErrorMessageDto(Exception e) {
        return ErrorMessageDto.builder()
                .message(e.getMessage())
                .description(String.valueOf(e.getCause()))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
