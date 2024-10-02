package school.faang.user_service.exception.goal.invitation;

import java.util.NoSuchElementException;

public class InvitationEntityNotFoundException extends NoSuchElementException {
    public InvitationEntityNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
