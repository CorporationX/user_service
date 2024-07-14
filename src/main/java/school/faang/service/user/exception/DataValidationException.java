package school.faang.service.user.exception;

public class DataValidationException extends IllegalArgumentException {
    public DataValidationException() {
        this("Data validation failed");
    }

    public DataValidationException(String message) {
        super(message);
    }
}
