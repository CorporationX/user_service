package school.faang.user_service.exception.tasksEntity.notFoundExceptions.event;

import school.faang.user_service.exception.tasksEntity.EntityNotFoundException;

public class EventNotFoundException extends EntityNotFoundException {
    public EventNotFoundException(String message) {
        super(message);
    }
}
