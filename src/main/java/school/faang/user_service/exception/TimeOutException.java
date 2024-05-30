package school.faang.user_service.exception;

public class TimeOutException extends RuntimeException {
    public TimeOutException(String message) {
        super(message);
    }
}
