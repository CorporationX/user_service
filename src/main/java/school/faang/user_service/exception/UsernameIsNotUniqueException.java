package school.faang.user_service.exception;

public class UsernameIsNotUniqueException extends RuntimeException {
    public UsernameIsNotUniqueException(String message) {
        super(message);
    }
}