package school.faang.user_service.exception;

public class AvatarGenerationException extends RuntimeException {
    public AvatarGenerationException(String message) {
        super(message);
    }

    public AvatarGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
