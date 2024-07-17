package school.faang.user_service.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(ExceptionMessage errorMessage, Object... arguments) {
        super(String.format(errorMessage.getMessage(), arguments));
    }

    public ValidationException(String foramtString, Object... arguments) {
        super(String.format(foramtString, arguments));
    }

    public ValidationException(String message) {
        super(message);
    }
}
