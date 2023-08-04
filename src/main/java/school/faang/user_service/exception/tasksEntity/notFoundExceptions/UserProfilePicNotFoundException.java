package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

public class UserProfilePicNotFoundException extends EntityNotFoundException {
    public UserProfilePicNotFoundException(String message) {
        super(message);
    }
}
