package school.faang.user_service.exception;

public class UserNotRegisteredAtEvent extends RuntimeException {
    public UserNotRegisteredAtEvent(long eventId, long userId) {
        super(String.format("User with id %d is not registered at the event with id %d", userId, eventId));
    }
}
