package school.faang.user_service.exception.premium;

public class PremiumNotActiveException extends RuntimeException {
    public PremiumNotActiveException(String message) {
        super(message);
    }
}
