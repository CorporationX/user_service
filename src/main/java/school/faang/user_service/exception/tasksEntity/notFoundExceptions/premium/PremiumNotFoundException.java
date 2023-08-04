package school.faang.user_service.exception.tasksEntity.notFoundExceptions.premium;

import school.faang.user_service.exception.tasksEntity.notFoundExceptions.EntityNotFoundException;

public class PremiumNotFoundException extends EntityNotFoundException {
    public PremiumNotFoundException(String message) {
        super(message);
    }
}
