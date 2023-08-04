package school.faang.user_service.exception.tasksEntity;

public abstract class InvalidFieldException extends RuntimeException {
    public InvalidFieldException(String message) {
        super(message);
    }
}
