package school.faang.user_service.controller.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Ошибка валидации входных данных
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                               HttpServletRequest req) {
        String errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> String.format("%s - %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        log.warn("Method argument not valid exception at {}, {}: {}",
                safeMethod(req), safeUri(req), errors);

        return ErrorResponseFactory.create(e, req, HttpStatus.BAD_REQUEST);
    }

    // Ошибка, когда не найден ресурс
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest req) {
        log.warn("Entity not found exception at {}, {}: {}",
                safeMethod(req), safeUri(req), e.getMessage());

        return ErrorResponseFactory.create(e, req, HttpStatus.NOT_FOUND);
    }

    // Ошибка доступа
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException e, HttpServletRequest req) {
        log.warn("Access denied ({}), {}, {}: {}",
                e.getClass().getSimpleName(), safeMethod(req), safeUri(req), e.getMessage());

        return ErrorResponseFactory.create(e, req, HttpStatus.FORBIDDEN);
    }

    // Плохие запросы клиентов
    @ExceptionHandler({
            DataValidationException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadInput(RuntimeException e, HttpServletRequest req) {
        log.warn("Client error ({}), {}, {}: {}",
                e.getClass().getSimpleName(), safeMethod(req), safeUri(req), e.getMessage());

        return ErrorResponseFactory.create(e, req, HttpStatus.BAD_REQUEST);
    }

    // Неожиданные ошибки
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedException(Exception e, HttpServletRequest req) {
        log.error("Unexpected error ({}) at {}, {}: {}",
                e.getClass().getSimpleName(), safeMethod(req), safeUri(req), e.getMessage(), e);

        return ErrorResponseFactory.create(e, req, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String safeMethod(HttpServletRequest req) {
        return Optional.ofNullable(req).map(HttpServletRequest::getMethod).orElse("N/A");
    }

    private String safeUri(HttpServletRequest req) {
        return Optional.ofNullable(req).map(HttpServletRequest::getRequestURI).orElse("N/A");
    }
}