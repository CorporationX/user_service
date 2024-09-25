package school.faang.user_service.exception;

public class PremiumAlreadyExistsException extends RuntimeException {
    public PremiumAlreadyExistsException(Long id) {
        super("Premium already exists with id " + id);
    }
}
