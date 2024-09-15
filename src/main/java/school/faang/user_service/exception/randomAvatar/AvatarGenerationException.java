package school.faang.user_service.exception.randomAvatar;

public class AvatarGenerationException extends RuntimeException{

    public AvatarGenerationException() {
        super();
    }

    public AvatarGenerationException(String message) {
        super(message);
    }
}
