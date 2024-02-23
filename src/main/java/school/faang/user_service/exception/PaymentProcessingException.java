package school.faang.user_service.exception;

public class PaymentProcessingException extends RuntimeException {
    public PaymentProcessingException(String msg) {
        super(msg);
    }
}
