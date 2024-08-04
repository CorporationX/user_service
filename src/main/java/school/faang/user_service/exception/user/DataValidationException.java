package school.faang.user_service.exception.user;

public class DataValidationException extends RuntimeException {

    public DataValidationException(String message) {
        super(message);
    }
}
