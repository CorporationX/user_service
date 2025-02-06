package school.faang.user_service.exception;

public class S3Exception extends RuntimeException {
    public S3Exception(String message, Throwable cause) {
        super(message, cause);
    }
}