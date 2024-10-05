package school.faang.user_service.controller.premium;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.exception.premium.PremiumValidationFailureException;
import school.faang.user_service.exception.premium.PremiumNotFoundException;

@Slf4j
@ControllerAdvice
public class PremiumControllerExceptionHandler {

    @ExceptionHandler(PremiumValidationFailureException.class)
    public ResponseEntity<ProblemDetail> handlePremiumValidationFailureException(PremiumValidationFailureException exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(PremiumNotFoundException.class)
    public ResponseEntity<ProblemDetail> premiumNotFoundExceptionHandler(PremiumNotFoundException exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage()));
    }
}
