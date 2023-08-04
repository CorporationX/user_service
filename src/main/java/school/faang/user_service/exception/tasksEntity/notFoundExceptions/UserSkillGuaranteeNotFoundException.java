package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

import school.faang.user_service.exception.tasksEntity.EntityNotFoundException;

public class UserSkillGuaranteeNotFoundException extends EntityNotFoundException {
    public UserSkillGuaranteeNotFoundException(String message) {
        super(message);
    }
}
