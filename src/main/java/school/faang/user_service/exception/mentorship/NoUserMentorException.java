package school.faang.user_service.exception.mentorship;

public class NoUserMentorException extends RuntimeException {
    public NoUserMentorException(String message) {
        super(message);
    }
}
