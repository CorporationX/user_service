package school.faang.user_service.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String msg) {
        super(msg);
    }
}
