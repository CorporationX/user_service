package school.faang.user_service.exception;

public class TimeHasNotPassedException extends RuntimeException{
    public TimeHasNotPassedException(String message) {
        super(message);
    }
}
