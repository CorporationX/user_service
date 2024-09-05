package school.faang.user_service.exception;

public class MentorshipNotFoundException extends RestRuntimeException{

    public MentorshipNotFoundException(String message) {
        super(message);
    }
}
