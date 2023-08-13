package school.faang.user_service.exception.notFoundExceptions.recommendation;

import school.faang.user_service.exception.EntityNotFoundException;

public class SkillOfferNotFoundException extends EntityNotFoundException {
    public SkillOfferNotFoundException(String message) {
        super(message);
    }
}
