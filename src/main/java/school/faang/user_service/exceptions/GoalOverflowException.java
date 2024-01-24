package school.faang.user_service.exceptions;

public class GoalOverflowException extends RuntimeException{
    public GoalOverflowException(String message) {
        super(message);
    }
}
