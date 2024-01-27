package school.faang.user_service.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String msg) {
        super(msg);
    }

}
