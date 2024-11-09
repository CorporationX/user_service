package school.faang.user_service.exception;

public class DataValidationException extends RuntimeException {
    public DataValidationException() {
        super("Couldn't create a skill: skill is already exists");
    }

    public DataValidationException(String message) {
        super(message);
    }
}
