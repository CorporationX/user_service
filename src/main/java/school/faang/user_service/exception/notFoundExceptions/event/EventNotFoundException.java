package school.faang.user_service.exception.notFoundExceptions.event;

import school.faang.user_service.exception.EntityNotFoundException;

public class EventNotFoundException extends EntityNotFoundException {
    public EventNotFoundException(String message) {
        super(message);
    }
}
