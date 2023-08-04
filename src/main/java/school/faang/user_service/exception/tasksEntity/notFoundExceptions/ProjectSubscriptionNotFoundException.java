package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

import school.faang.user_service.exception.tasksEntity.EntityNotFoundException;

public class ProjectSubscriptionNotFoundException extends EntityNotFoundException {
    public ProjectSubscriptionNotFoundException(String message) {
        super(message);
    }
}
