package school.faang.user_service.exception.goal;

public class UserReachedMaxGoalsException extends RuntimeException {

    public UserReachedMaxGoalsException(String message) {
        super(message);
    }
}
