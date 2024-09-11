package school.faang.user_service.exception.promotion;

public class PromotionCheckException extends IllegalArgumentException {
    public PromotionCheckException(String message, Object... args) {
        super(String.format(message, args));
    }
}
