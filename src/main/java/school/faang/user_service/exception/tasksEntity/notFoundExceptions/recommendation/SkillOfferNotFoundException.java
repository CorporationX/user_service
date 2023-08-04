package school.faang.user_service.exception.tasksEntity.notFoundExceptions.recommendation;

import school.faang.user_service.exception.tasksEntity.EntityNotFoundException;

public class SkillOfferNotFoundException extends EntityNotFoundException {
    public SkillOfferNotFoundException(String message) {
        super(message);
    }
}
