package school.faang.user_service.exeptions;

public class UserAreNotRegisteredAtEventExeption extends RuntimeException{
    public UserAreNotRegisteredAtEventExeption(long eventId, long userId) {
        super(String.format("User with id %d aren't registered at event with id %d", userId, eventId));
    }
}
