package school.faang.user_service.exception;

public class FileUploadException extends RuntimeException {
    public FileUploadException(String msg) {
        super(msg);
    }

    public FileUploadException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
