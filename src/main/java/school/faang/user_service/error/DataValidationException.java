package school.faang.user_service.error;

public class DataValidationException extends RuntimeException {
   public DataValidationException(String message) {
        super(message);
    }
}
