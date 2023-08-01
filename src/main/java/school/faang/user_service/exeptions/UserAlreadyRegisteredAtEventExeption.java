package school.faang.user_service.exeptions;

public class UserAlreadyRegisteredAtEventExeption extends RuntimeException {
    public UserAlreadyRegisteredAtEventExeption(long eventId, long userId) {
        super(String.format("User with id %d already registered at event with id %d", userId, eventId));
    }
}

