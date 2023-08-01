package school.faang.user_service.util.exception;

public class GoalNotFoundException extends RuntimeException{
    public GoalNotFoundException(String message) {
        super(message);
    }
}
