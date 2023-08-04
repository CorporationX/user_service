package school.faang.user_service.exception.tasksEntity.notFoundExceptions.recommendation;

import school.faang.user_service.exception.tasksEntity.notFoundExceptions.EntityNotFoundException;

public class RecommendationNotFoundException extends EntityNotFoundException {
    public RecommendationNotFoundException(String message) {
        super(message);
    }
}
