package school.faang.user_service.exception;

public class MaxActiveGoalsReachedException extends RuntimeException {
    public MaxActiveGoalsReachedException(String message) {
        super(message);
    }
}
