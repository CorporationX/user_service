package school.faang.user_service.exception;

public class DataValidationException extends IllegalArgumentException {
    public DataValidationException() {
        this("Data validation failed");
    }

    public DataValidationException(String message) {
        super(message);
    }
}
