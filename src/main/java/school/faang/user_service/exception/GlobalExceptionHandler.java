package school.faang.user_service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.exception.premium.PremiumIllegalArgumentException;
import school.faang.user_service.exception.payment.PaymentException;
import school.faang.user_service.exception.promotion.PromotionIllegalArgumentException;

/**
 * @author Evgenii Malkov
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PremiumIllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(PremiumIllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<String> handleRuntimeArgument(PaymentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PromotionIllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(PromotionIllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
