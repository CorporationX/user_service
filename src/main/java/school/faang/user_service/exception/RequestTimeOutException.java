package school.faang.user_service.exception;

public class RequestTimeOutException extends RuntimeException {
    public RequestTimeOutException(String message) {
        super(message);
    }
}
