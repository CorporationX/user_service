package school.faang.user_service.exception;

public class EmptyFileException extends FileFormatException {
    public EmptyFileException(String message) {
        super(message);
    }
}
