package school.faang.user_service.exception;

public class EventProcessingException extends RuntimeException {
    public EventProcessingException(String message) {
        super(message);
    }
}