package school.faang.user_service.exception;

public class FileSizeLimitExceededException extends DataValidationException {
    public FileSizeLimitExceededException(String message) {
        super(message);
    }
}