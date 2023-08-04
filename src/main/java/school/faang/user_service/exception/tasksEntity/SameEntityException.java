package school.faang.user_service.exception.tasksEntity;

public class SameEntityException extends RuntimeException{
    public SameEntityException(String message) {
        super(message);
    }
}
