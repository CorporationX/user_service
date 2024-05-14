package school.faang.user_service.exceptions;

public class UserGoalsValidationException extends RuntimeException{
    public UserGoalsValidationException(String message) {
        super(message);
    }
}
