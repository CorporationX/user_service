package school.faang.user_service.exception.notFoundExceptions;

import school.faang.user_service.exception.EntityNotFoundException;

public class ProjectSubscriptionNotFoundException extends EntityNotFoundException {
    public ProjectSubscriptionNotFoundException(String message) {
        super(message);
    }
}
