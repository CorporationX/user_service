package school.faang.user_service.exception.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.dto.ErrorResponse;
import school.faang.user_service.dto.ValidationErrorDTO;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.FileUploadException;
import school.faang.user_service.exception.NonUniqueFieldsException;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${services.user-service.name}")
    private String serviceName;


    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e) {
        log.info("DataValidationException found and occurred: {}", e.getMessage());
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage("Something went wrong...")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }


    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFileUploadException(FileUploadException exception) {
        log.error("Error occurred: {}", exception.getMessage());
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage("Something went wrong...")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoSuchElementException(NoSuchElementException e) {
        log.info("NoSuchElementException found and occurred: {}", e.getMessage());
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage("Something went wrong...")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(NonUniqueFieldsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNonUniqueFieldsException(NonUniqueFieldsException e) {
        log.info("NonUniqueFieldsException found and occurred: {}", e.getMessage());
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage("Something went wrong...")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<ValidationErrorDTO> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationErrorDTO(error.getField(), Optional.ofNullable(error.getDefaultMessage()).orElse("Unknown error")))
                .collect(Collectors.toList());
        log.error("Constraint violation error: {}", errors);

        return ErrorResponse.builder()
                .serviceName(serviceName)
                .fieldErrors(errors)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        log.info("IllegalArgumentException found and occurred: {}", e.getMessage());
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage("Something went wrong...")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler({NonUniqueFieldsException.class, EntityNotFoundException.class, DataValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataExceptions(RuntimeException exception) {
        String message = exception.getMessage();
        log.error("Error: {}", message);
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage(message)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(EntityNotFoundException e) {
        log.info("EntityNotFoundException occurred: {}", e.getMessage());
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage("Something went wrong...")
                .status(HttpStatus.NOT_FOUND.value())
                .build();
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException e) {
        log.info("AuthenticationException occurred: {}", e.getMessage());
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage("Something went wrong...")
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();
    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        log.info("RuntimeException occurred: {}", e.getMessage());
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage("Something went wrong...")
                .status(HttpStatus.REQUEST_TIMEOUT.value())
                .build();
    }
}
