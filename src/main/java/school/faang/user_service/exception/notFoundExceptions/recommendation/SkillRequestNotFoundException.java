package school.faang.user_service.exception.notFoundExceptions.recommendation;

import school.faang.user_service.exception.EntityNotFoundException;

public class SkillRequestNotFoundException extends EntityNotFoundException {
    public SkillRequestNotFoundException(String message) {
        super(message);
    }
}
