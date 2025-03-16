package school.faang.user_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.PaymentProceedException;
import school.faang.user_service.exception.UserNotFoundException;

@RestControllerAdvice
@Slf4j
public class UsersServiceExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDataValidationException(DataValidationException e) {
        String message = e.getMessage();

        log.error("DataValidationException caught: {}", message);
        return message;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleUserNotFoundException(UserNotFoundException e) {
        String message = e.getMessage();

        log.error("UserNotFoundException caught: {}", message);
        return message;
    }

    @ExceptionHandler(PaymentProceedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlePaymentProceedException(PaymentProceedException e) {
        String message = e.getMessage();

        log.error("PaymentProceedException caught: {}", message);
        return message;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException e) {
        String message = e.getMessage();

        log.error("RuntimeException caught: {}", message);
        return message;
    }
}
