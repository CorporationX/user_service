package school.faang.user_service.exception.notFoundExceptions;

import school.faang.user_service.exception.EntityNotFoundException;

public class MentorshipRequestNotFoundException extends EntityNotFoundException {
    private static final String msg = "Mentorship request not found";
    public MentorshipRequestNotFoundException() {
        super(msg);
    }
}
