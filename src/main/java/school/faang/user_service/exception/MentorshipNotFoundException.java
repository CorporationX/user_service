package school.faang.user_service.exception;

public class MentorshipNotFoundException extends UserNotFoundException{

    public MentorshipNotFoundException(String message) {
        super(message);
    }
}
