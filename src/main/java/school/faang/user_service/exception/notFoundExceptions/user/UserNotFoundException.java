package school.faang.user_service.exception.notFoundExceptions.user;

import school.faang.user_service.exception.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
