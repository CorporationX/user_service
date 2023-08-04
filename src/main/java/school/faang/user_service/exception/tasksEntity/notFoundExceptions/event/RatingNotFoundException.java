package school.faang.user_service.exception.tasksEntity.notFoundExceptions.event;

import school.faang.user_service.exception.tasksEntity.notFoundExceptions.EntityNotFoundException;

public class RatingNotFoundException extends EntityNotFoundException {
    public RatingNotFoundException(String message) {
        super(message);
    }
}
