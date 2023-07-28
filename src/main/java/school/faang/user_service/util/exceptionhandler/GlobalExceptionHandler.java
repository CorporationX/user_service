package school.faang.user_service.util.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.dto.response.ErrorResponse;
import school.faang.user_service.util.exception.DataValidationException;
import school.faang.user_service.util.exception.UserNotFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleException(DataValidationException e){
        log.error(e.getMessage());

        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserNotFoundException e){
        log.error(e.getMessage());

        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                e.getMessage(), LocalDateTime.now()), HttpStatus.NOT_FOUND);
    }
}
