package school.faang.user_service.exception;

public class CreateInvitationException extends RuntimeException{
    public CreateInvitationException(String message) {
        super(message);
    }

    public CreateInvitationException() {
    }
}
