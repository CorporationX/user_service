package school.faang.user_service.exception;

public class NonUniqueFieldsException extends RuntimeException {
    public NonUniqueFieldsException(String message) {
        super(message);
    }
}
