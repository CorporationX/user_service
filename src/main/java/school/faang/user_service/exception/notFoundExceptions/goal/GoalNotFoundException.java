package school.faang.user_service.exception.notFoundExceptions.goal;

import school.faang.user_service.exception.EntityNotFoundException;

public class GoalNotFoundException extends EntityNotFoundException {
    public GoalNotFoundException(String message) {
        super(message);
    }
}
