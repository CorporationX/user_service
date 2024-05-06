package school.faang.user_service.exceptions.mentorship;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
