package school.faang.user_service.exception;

public class MentorshipAlreadyExistsException extends RuntimeException {
    public MentorshipAlreadyExistsException(String message) {
        super(message);
    }
}