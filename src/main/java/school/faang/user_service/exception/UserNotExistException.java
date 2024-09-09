package school.faang.user_service.exception;

public class UserNotExistException extends RuntimeException{

    public UserNotExistException(String message) {
        super(message);
    }
}
