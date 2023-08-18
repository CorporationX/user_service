package school.faang.user_service.exception;

public class GoalValidationException extends RuntimeException{
    public GoalValidationException(String message) {
        super(message);
    }

    public GoalValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}