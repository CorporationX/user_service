package school.faang.user_service.exeption;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String s) {
    }
    public DataValidationException(long followerId, long followeeId) {
        super(String.format("User with id %d already follow user with id %d", followerId, followeeId));
    }
}
