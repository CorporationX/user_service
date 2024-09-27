package school.faang.user_service.exception;

public class FileDeleteException extends RuntimeException {
    public FileDeleteException(String message) {
        super(message);
    }
}