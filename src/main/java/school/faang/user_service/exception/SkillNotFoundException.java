package school.faang.user_service.exception;

public class SkillNotFoundException extends RuntimeException {
    public SkillNotFoundException(String skill) {
        super("Skill not found " + skill);
    }
}
