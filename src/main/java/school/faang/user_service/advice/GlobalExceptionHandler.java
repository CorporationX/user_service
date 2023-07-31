package school.faang.user_service.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DeactivationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeactivationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String deactivatingError(DeactivationException e) {
        return e.getMessage();
    }
}
