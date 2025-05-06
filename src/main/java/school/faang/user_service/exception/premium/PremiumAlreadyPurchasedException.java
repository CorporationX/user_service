package school.faang.user_service.exception.premium;

public class PremiumAlreadyPurchasedException extends RuntimeException {
    public PremiumAlreadyPurchasedException(String message) {
        super(message);
    }
}
