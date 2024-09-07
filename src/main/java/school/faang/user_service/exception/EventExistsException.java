package school.faang.user_service.exception;

public class EventExistsException extends RuntimeException{

    public EventExistsException(String message) {
        super(message);
    }
}
