package school.faang.user_service.exception;

public class DataValidationException extends RuntimeException {

    public DataValidationException(MessageError error) {
        super(error.getMessage());
    }

    public DataValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

