package school.faang.user_service.controller.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import school.faang.user_service.exception.DataValidationException;

@ControllerAdvice
public class UserServiceExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler()
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String exceptionMessage = ex.getMessage();
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof DataValidationException) {
            return handleExceptionInternal(ex, exceptionMessage, headers, HttpStatus.BAD_REQUEST, request);
        } else {
            return handleExceptionInternal(ex, exceptionMessage, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }
}