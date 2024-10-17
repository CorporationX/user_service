package school.faang.user_service.exception;

public class ExistingPurchaseException extends RuntimeException {
    public ExistingPurchaseException(String message) {
        super(message);
    }
}
