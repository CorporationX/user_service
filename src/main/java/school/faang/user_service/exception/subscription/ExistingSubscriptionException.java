package school.faang.user_service.exception.subscription;

public class ExistingSubscriptionException extends RuntimeException {
    public ExistingSubscriptionException(String messsage) {
        super(messsage);
    }
}
