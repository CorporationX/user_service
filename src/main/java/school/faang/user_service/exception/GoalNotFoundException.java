package school.faang.user_service.exception;

public class GoalNotFoundException extends RuntimeException{
    public GoalNotFoundException(String message) {
        super(message);
    }
}
