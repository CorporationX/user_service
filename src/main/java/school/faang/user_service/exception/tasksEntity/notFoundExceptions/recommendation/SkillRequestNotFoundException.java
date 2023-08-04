package school.faang.user_service.exception.tasksEntity.notFoundExceptions.recommendation;

import school.faang.user_service.exception.tasksEntity.EntityNotFoundException;

public class SkillRequestNotFoundException extends EntityNotFoundException {
    public SkillRequestNotFoundException(String message) {
        super(message);
    }
}
