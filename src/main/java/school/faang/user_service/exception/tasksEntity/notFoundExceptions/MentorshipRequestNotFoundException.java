package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

import school.faang.user_service.exception.tasksEntity.EntityNotFoundException;

public class MentorshipRequestNotFoundException extends EntityNotFoundException {
    private static final String msg = "Mentorship request not found";
    public MentorshipRequestNotFoundException() {
        super(msg);
    }
}
