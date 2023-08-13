package school.faang.user_service.exception.notFoundExceptions;

import school.faang.user_service.exception.EntityNotFoundException;

public class SkillNotFoundException extends EntityNotFoundException {
    public SkillNotFoundException(String message) {
        super(message);
    }
}
