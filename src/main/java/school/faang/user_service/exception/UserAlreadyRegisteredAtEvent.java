package school.faang.user_service.exception;
public class UserAlreadyRegisteredAtEvent extends RuntimeException {
    public UserAlreadyRegisteredAtEvent(long userId, long eventId) {
        super(String.format("User with id %d already registered at event with id %d", userId, eventId));
    }
}