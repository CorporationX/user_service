package school.faang.user_service.exception;

public class ApplicationException extends RuntimeException {
    public ApplicationException(String format, Object... args) {
        super(String.format(format, args));
    }
}
