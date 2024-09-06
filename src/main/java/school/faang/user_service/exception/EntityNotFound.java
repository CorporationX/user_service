package school.faang.user_service.exception;

public class EntityNotFound extends RuntimeException {
    public EntityNotFound(String message) {
        super(message);
    }
}
