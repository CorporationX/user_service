package school.faang.user_service.handler.exception;

public class FileReadException extends RuntimeException {

    public FileReadException(String message) {
        super(message);
    }
}