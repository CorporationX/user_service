package school.faang.user_service.exception.tasksEntity.notFoundExceptions.goal;

import school.faang.user_service.exception.tasksEntity.notFoundExceptions.EntityNotFoundException;

public class GoalInvitationNotFoundException extends EntityNotFoundException {
    public GoalInvitationNotFoundException(String message) {
        super(message);
    }
}
