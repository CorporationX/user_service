package school.faang.user_service.exception;

public class FileParseException extends RuntimeException {
    public FileParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileParseException(String message) {
        super(message);
    }
}
