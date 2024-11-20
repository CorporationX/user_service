package school.faang.user_service.exception;

public class RejectRequestFailedException extends RuntimeException {
    public RejectRequestFailedException(String message) {
        super(message);
    }
}
