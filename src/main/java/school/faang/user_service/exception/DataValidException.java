package school.faang.user_service.exception;

public class DataValidException extends RuntimeException {
    public DataValidException(String message) {
        super(message);
    }
}