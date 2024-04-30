package school.faang.user_service.exception;

public class TooManyGoalsException extends RuntimeException {
    public TooManyGoalsException() {
        super("Too many goals...");
    }
}
