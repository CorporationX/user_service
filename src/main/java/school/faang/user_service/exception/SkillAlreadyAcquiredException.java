package school.faang.user_service.exception;

public class SkillAlreadyAcquiredException extends RuntimeException {
    public SkillAlreadyAcquiredException(String message) {
        super(message);
    }
}
