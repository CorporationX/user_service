package school.faang.user_service.exception;

public class InvalidFileFormatException extends DataValidationException {
    public InvalidFileFormatException(String message) {
        super(message);
    }
}