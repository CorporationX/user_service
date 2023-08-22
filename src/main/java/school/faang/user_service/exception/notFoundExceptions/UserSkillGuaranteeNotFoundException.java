package school.faang.user_service.exception.notFoundExceptions;

import school.faang.user_service.exception.EntityNotFoundException;

public class UserSkillGuaranteeNotFoundException extends EntityNotFoundException {
    public UserSkillGuaranteeNotFoundException(String message) {
        super(message);
    }
}
