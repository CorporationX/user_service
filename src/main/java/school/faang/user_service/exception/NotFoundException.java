package school.faang.user_service.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String mes) {
        super(mes);
    }
}
