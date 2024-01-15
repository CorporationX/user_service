package school.faang.user_service.handler;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException exception, HttpServletRequest request) {
        log.error("DataValidationException: {}", exception.getMessage());
        return new ErrorResponse(request.getRequestURI(), HttpStatus.BAD_REQUEST, "DataValidationException", exception.getMessage());
    }


    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus()
    public ErrorResponse handleUserNotFoundException(UserNotFoundException exception, HttpServletRequest request) {
        log.error("UserNotFoundException: {}", exception.getMessage());
        return new ErrorResponse(request.getRequestURI(), HttpStatus.NOT_FOUND, "UserNotFoundException", exception.getMessage());
    }
}
