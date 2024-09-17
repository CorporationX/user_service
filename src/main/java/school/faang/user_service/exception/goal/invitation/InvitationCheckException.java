package school.faang.user_service.exception.goal.invitation;

public class InvitationCheckException extends RuntimeException {
    public InvitationCheckException(String message, Object... args) {
        super(String.format(message, args));
    }
}
