package school.faang.user_service.exception;

public class MentorshipNotFoundException extends EntityNotFoundException {
    public MentorshipNotFoundException(String message) {
        super(message);
    }
}
