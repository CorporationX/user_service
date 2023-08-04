package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

public class SkillNotFoundException extends EntityNotFoundException{
    public SkillNotFoundException(String message) {
        super(message);
    }
}
