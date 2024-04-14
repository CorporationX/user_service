package school.faang.user_service.handler.exception;

public class FileOperationException extends RuntimeException {

    public FileOperationException(String message) {
        super(message);
    }
}