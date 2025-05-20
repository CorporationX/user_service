package school.faang.user_service.exception;

public class DataValidationException extends Throwable {
    public DataValidationException() {
        super("Data validation failed");
    }

    public DataValidationException(String message) {
        super(message);
    }
}
