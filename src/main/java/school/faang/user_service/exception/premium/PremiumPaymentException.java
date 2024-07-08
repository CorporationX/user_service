package school.faang.user_service.exception.premium;

/**
 * @author Evgenii Malkov
 */
public class PremiumPaymentException extends RuntimeException {
    public PremiumPaymentException(String message) {
        super(message);
    }
}
