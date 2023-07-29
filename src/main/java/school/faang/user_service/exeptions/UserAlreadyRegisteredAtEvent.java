package school.faang.user_service.exeptions;

public class UserAlreadyRegisteredAtEvent extends RuntimeException {
    public UserAlreadyRegisteredAtEvent(long eventId, long userId) {
        super(String.format("User with id %d already registered at event with id %d", userId, eventId));
    }
}

