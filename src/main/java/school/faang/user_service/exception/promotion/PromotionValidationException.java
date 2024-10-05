package school.faang.user_service.exception.promotion;

public class PromotionValidationException extends IllegalArgumentException {
    public PromotionValidationException(String message, Object... args) {
        super(String.format(message, args));
    }
}
