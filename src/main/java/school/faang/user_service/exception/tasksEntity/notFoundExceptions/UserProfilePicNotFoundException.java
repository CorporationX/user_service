package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

import school.faang.user_service.exception.tasksEntity.EntityNotFoundException;

public class UserProfilePicNotFoundException extends EntityNotFoundException {
    public UserProfilePicNotFoundException(String message) {
        super(message);
    }
}
