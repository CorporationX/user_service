package school.faang.user_service.exception.event;

public class EventPublishingException extends RuntimeException {

    public EventPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}
