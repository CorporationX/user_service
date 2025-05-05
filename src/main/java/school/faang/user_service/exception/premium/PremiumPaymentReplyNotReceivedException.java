package school.faang.user_service.exception.premium;

public class PremiumPaymentReplyNotReceivedException extends RuntimeException {
    public PremiumPaymentReplyNotReceivedException(String message) {
        super(message);
    }
}
