package school.faang.user_service.exceptions;

public class UserWasNotFoundException extends RuntimeException {
    public UserWasNotFoundException(String message) {
        super(message);
    }
}