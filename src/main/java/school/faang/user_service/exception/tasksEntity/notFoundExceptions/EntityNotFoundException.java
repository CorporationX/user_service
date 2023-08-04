package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

public abstract class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
