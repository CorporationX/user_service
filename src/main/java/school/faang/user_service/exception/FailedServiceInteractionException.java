package school.faang.user_service.exception;

public class FailedServiceInteractionException extends RuntimeException {

    public FailedServiceInteractionException(String message) {
        super(message);
    }
}
