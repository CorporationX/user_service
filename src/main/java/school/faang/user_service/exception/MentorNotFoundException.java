package school.faang.user_service.exception;

public class MentorNotFoundException extends UserNotFoundException {

    public MentorNotFoundException(String message) {
        super(message);
    }
}
