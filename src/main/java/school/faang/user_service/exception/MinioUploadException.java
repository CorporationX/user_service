package school.faang.user_service.exception;

public class MinioUploadException extends RuntimeException {
    public MinioUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}