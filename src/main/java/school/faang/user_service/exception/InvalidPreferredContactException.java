package school.faang.user_service.exception;

public class InvalidPreferredContactException extends RuntimeException {
    public InvalidPreferredContactException(String invalidValue) {
        super("Invalid preferred contact method: " + invalidValue
                + ". Allowed values are EMAIL, SMS, TELEGRAM.");
    }
}
