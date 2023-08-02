package school.faang.user_service.exeption;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String s) {
        super(s);
    }
}
