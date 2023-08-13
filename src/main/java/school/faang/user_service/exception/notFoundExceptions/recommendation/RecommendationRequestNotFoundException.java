package school.faang.user_service.exception.notFoundExceptions.recommendation;

import school.faang.user_service.exception.EntityNotFoundException;

public class RecommendationRequestNotFoundException extends EntityNotFoundException {
    public RecommendationRequestNotFoundException(String message) {
        super(message);
    }
}
