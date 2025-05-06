package school.faang.user_service.exception.premium;

public class PremiumTypeNotFoundException extends RuntimeException {
    public PremiumTypeNotFoundException(String message) {
        super(message);
    }
}
