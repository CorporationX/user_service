package school.faang.user_service.exception;

public class MessageRequestException extends RuntimeException {
    public MessageRequestException(String message) {
        super(message);
    }
}
