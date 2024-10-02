package school.faang.user_service.exception;

public class BadRequestException extends ApplicationException {
    public BadRequestException(String format, Object... args) {
        super(format, args);
    }
}
