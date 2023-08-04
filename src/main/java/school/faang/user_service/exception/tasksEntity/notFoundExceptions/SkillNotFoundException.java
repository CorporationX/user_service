package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

import school.faang.user_service.exception.tasksEntity.EntityNotFoundException;

public class SkillNotFoundException extends EntityNotFoundException {
    public SkillNotFoundException(String message) {
        super(message);
    }
}
