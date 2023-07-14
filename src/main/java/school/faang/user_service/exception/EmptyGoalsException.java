package school.faang.user_service.exception;

public class EmptyGoalsException extends RuntimeException {

    public EmptyGoalsException(String message) {
        super(message);
    }
}
