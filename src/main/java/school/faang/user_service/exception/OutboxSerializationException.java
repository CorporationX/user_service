package school.faang.user_service.exception;

public class OutboxSerializationException extends RuntimeException {
    public OutboxSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
