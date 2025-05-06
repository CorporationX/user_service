package school.faang.user_service.exception.promotion;

public class DuplicatePromotionException extends RuntimeException {
    public DuplicatePromotionException(String message) {
        super(message);
    }
}
