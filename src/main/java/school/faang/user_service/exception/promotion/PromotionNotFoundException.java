package school.faang.user_service.exception.promotion;

import java.util.NoSuchElementException;

public class PromotionNotFoundException extends NoSuchElementException {
    public PromotionNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
