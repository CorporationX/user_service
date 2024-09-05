package school.faang.user_service.exception;



public class UserNotFoundException extends RestRuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

