package school.faang.user_service.exception.promotion;

public class PromotionNotFoundException extends RuntimeException {
    public PromotionNotFoundException(String message) {
        super(message);
    }
}
