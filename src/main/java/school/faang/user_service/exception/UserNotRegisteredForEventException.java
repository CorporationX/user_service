package school.faang.user_service.exception;

public class UserNotRegisteredForEventException extends RuntimeException {
    public UserNotRegisteredForEventException(String message) {
        super(message);
    }
}
