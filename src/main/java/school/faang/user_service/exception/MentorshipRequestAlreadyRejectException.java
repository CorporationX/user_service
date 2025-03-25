package school.faang.user_service.exception;

public class MentorshipRequestAlreadyRejectException extends RuntimeException {
    public MentorshipRequestAlreadyRejectException(String message) {
        super(message);
    }
}