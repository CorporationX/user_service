package school.faang.user_service.exception;

public class PaymentFailureException extends RuntimeException {
    public PaymentFailureException(String message, Object... args) {
        super(String.format(message, args));
    }
}
