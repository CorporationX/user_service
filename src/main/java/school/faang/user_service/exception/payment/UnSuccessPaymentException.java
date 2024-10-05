package school.faang.user_service.exception.payment;

public class UnSuccessPaymentException extends RuntimeException{
    public UnSuccessPaymentException(String message, Object... args) {
        super(String.format(message, args));
    }
}
