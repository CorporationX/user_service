package school.faang.user_service.exception;

public class DeactivationException extends RuntimeException {

    public DeactivationException(String message, long id) {
        super(message + " " + id);
    }
}
