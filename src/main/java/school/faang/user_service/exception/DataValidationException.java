package school.faang.user_service.exception;

public class DataValidationException extends RuntimeException {
    public DataValidationException() {
        super("Такой skill уже существует");
    }
}
