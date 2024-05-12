package school.faang.user_service.exception;

public class DataValidationExeption extends RuntimeException {
    public DataValidationExeption (String message) {
        super(message);
    }
}
