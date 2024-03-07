package school.faang.user_service.exception;

public class GoalOverflowException extends RuntimeException{
    public GoalOverflowException(String message) {
        super(message);
    }
}
