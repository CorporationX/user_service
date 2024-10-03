package school.faang.user_service.exception.user;

public class EntitySaveException extends RuntimeException {
    public EntitySaveException(String message, Throwable e) {
        super(message, e);
    }
}
