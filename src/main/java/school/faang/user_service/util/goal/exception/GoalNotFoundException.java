package school.faang.user_service.util.goal.exception;

public class GoalNotFoundException extends RuntimeException{
    public GoalNotFoundException(String message) {
        super(message);
    }
}
