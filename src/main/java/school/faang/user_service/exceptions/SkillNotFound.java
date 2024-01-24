package school.faang.user_service.exceptions;

public class SkillNotFound extends RuntimeException {
    public SkillNotFound(String message) {
        super(message);
    }
}
