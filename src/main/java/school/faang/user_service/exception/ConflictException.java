package school.faang.user_service.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(MessageError messageError) {
        super(messageError.getMessage());
    }
}
