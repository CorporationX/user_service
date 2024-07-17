package school.faang.user_service.exception.payment;

/**
 * @author Evgenii Malkov
 */
public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable e) {
        super(message, e);
    }
}
