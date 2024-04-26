package school.faang.user_service.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(MessageError error) { //TODO EntityNotFoundException назвать
        super(error.getMessage());

    }
}
