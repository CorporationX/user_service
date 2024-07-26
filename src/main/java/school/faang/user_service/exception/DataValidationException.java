package school.faang.user_service.exception;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        super(message);
    }

    public DataValidationException(MessageError messageError) {
        super(messageError.getMessage());
    }
    public DataValidationException(String message) {
        super(message);
    }
}
