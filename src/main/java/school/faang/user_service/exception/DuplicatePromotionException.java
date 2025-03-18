package school.faang.user_service.exception;

public class DuplicatePromotionException extends RuntimeException {
    public DuplicatePromotionException(String message) {
        super(message);
    }
}
