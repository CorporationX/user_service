package school.faang.user_service.exception;

public class EventNotFoundException extends EntityNotFoundException{
    public EventNotFoundException(String message) {
        super(message);
    }
}
