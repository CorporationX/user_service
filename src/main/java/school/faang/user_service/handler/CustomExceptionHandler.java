package school.faang.user_service.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import school.faang.user_service.exception.ErrorResponse;
import school.faang.user_service.exception.RequestValidationException;

import java.time.LocalDateTime;


@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<Object> handleRequestValidationException(RequestValidationException e, HttpServletRequest rq) {
        ErrorResponse errResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .url(rq.getRequestURI())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Invalid Request")
            .message(e.getMessage())
            .build();

        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }
}