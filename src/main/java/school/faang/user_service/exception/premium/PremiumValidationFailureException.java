package school.faang.user_service.exception.premium;

public class PremiumValidationFailureException extends RuntimeException {
    public PremiumValidationFailureException(String message, Object... args) {
        super(String.format(message, args));
    }
}
