package school.faang.user_service.exception.subscription;

public class SubscriptionAlreadyExistsException extends RuntimeException {
    public SubscriptionAlreadyExistsException(String message) {
        super(message);
    }
}
