package school.faang.user_service.exception;

public class RestRuntimeException extends RuntimeException {
    public RestRuntimeException(String message) {
        super(message);
    }
}
