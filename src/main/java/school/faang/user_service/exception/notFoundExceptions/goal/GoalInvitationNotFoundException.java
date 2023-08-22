package school.faang.user_service.exception.notFoundExceptions.goal;

import school.faang.user_service.exception.EntityNotFoundException;

public class GoalInvitationNotFoundException extends EntityNotFoundException {
    public GoalInvitationNotFoundException(String message) {
        super(message);
    }
}
