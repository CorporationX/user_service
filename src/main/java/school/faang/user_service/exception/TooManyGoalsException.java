package school.faang.user_service.exception;

public class TooManyGoalsException extends RuntimeException {
    public TooManyGoalsException(String s) {
        super("Too many goals...");
    }
}
