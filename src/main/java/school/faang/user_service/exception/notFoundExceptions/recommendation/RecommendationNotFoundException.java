package school.faang.user_service.exception.notFoundExceptions.recommendation;

import school.faang.user_service.exception.EntityNotFoundException;

public class RecommendationNotFoundException extends EntityNotFoundException {
    public RecommendationNotFoundException(String message) {
        super(message);
    }
}
