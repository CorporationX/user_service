package school.faang.user_service.exception.notFoundExceptions;

import school.faang.user_service.exception.EntityNotFoundException;

public class UserProfilePicNotFoundException extends EntityNotFoundException {
    public UserProfilePicNotFoundException(String message) {
        super(message);
    }
}
