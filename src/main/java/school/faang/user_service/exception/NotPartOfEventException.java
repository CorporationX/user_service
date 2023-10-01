package school.faang.user_service.exception;

public class NotPartOfEventException extends RuntimeException {
    public NotPartOfEventException(String message) {
        super(message);
    }
}
