package school.faang.user_service.exception;

public class EventPublishingException extends RuntimeException {

    public EventPublishingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventPublishingException(String message) {
        super(message);
    }
}
