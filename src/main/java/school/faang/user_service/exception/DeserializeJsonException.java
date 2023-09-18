package school.faang.user_service.exception;

public class DeserializeJsonException extends RuntimeException {
    public DeserializeJsonException(String message) {
        super(message);
    }
}
