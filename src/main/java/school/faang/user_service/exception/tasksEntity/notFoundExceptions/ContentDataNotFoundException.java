package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

import school.faang.user_service.exception.tasksEntity.EntityNotFoundException;

public class ContentDataNotFoundException extends EntityNotFoundException {
    public ContentDataNotFoundException(String message) {
        super(message);
    }
}
