package school.faang.user_service.exception;

public class RegistrationUserForEventException extends RuntimeException {
    public RegistrationUserForEventException(String errorMessage) {
        super(errorMessage);
    }
}
