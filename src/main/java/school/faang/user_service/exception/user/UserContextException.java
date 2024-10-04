package school.faang.user_service.exception.user;

public class UserContextException extends IllegalArgumentException {
    public UserContextException(String message, Object... args) {
        super(String.format(message, args));
    }
}
