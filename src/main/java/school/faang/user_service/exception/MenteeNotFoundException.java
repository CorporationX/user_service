package school.faang.user_service.exception;

public class MenteeNotFoundException extends UserNotFoundException {
    public MenteeNotFoundException(String message) {
        super(message);
    }
}
