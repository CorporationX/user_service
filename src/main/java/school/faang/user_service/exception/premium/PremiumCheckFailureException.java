package school.faang.user_service.exception.premium;

public class PremiumCheckFailureException extends RuntimeException {
    public PremiumCheckFailureException(String message, Object... args) {
        super(String.format(message, args));
    }
}
