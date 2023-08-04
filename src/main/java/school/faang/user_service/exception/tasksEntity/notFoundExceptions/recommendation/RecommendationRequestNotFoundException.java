package school.faang.user_service.exception.tasksEntity.notFoundExceptions.recommendation;

import school.faang.user_service.exception.tasksEntity.notFoundExceptions.EntityNotFoundException;

public class RecommendationRequestNotFoundException extends EntityNotFoundException {
    public RecommendationRequestNotFoundException(String message) {
        super(message);
    }
}
