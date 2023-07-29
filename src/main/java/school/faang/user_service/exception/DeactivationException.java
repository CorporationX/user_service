package school.faang.user_service.exception;

public class DeactivationException extends RuntimeException {
    private final long id;

    public DeactivationException(String message, long id) {
        super(message);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
