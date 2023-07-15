package school.faang.user_service.exception.mentorship;

public class MenteeDoesNotExist extends RuntimeException {
    public MenteeDoesNotExist(String message) {
        super(message);
    }
}
