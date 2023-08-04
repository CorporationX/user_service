package school.faang.user_service.exception.tasksEntity.notFoundExceptions.goal;

import school.faang.user_service.exception.tasksEntity.EntityNotFoundException;

public class GoalNotFoundException extends EntityNotFoundException {
    public GoalNotFoundException(String message) {
        super(message);
    }
}
