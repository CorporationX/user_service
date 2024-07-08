package school.faang.user_service.exception;

import school.faang.user_service.exception.recommendation.RecommendationError;

public class EntityException extends RuntimeException{
    private final String error;
    public EntityException(RecommendationError error) {
        super(error.getMessage());
        this.error = error.getMessage();
    }
}
