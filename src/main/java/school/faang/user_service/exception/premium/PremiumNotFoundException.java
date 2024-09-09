package school.faang.user_service.exception.premium;

import java.util.NoSuchElementException;

public class PremiumNotFoundException extends NoSuchElementException {
    public PremiumNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
