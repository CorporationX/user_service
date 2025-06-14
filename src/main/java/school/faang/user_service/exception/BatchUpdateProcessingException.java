package school.faang.user_service.exception;

public class BatchUpdateProcessingException extends RuntimeException {
    public BatchUpdateProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
