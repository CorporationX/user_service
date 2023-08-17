package school.faang.user_service.exception.notFoundExceptions.premium;

import school.faang.user_service.exception.EntityNotFoundException;

public class PremiumNotFoundException extends EntityNotFoundException {
    public PremiumNotFoundException(String message) {
        super(message);
    }
}
