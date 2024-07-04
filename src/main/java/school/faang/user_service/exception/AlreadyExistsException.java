package school.faang.user_service.exception;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(MessageError messageError) {
        super(messageError.getMessage());
    }
}
