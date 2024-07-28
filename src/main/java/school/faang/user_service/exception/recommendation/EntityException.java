package school.faang.user_service.exception.recommendation;

public class EntityException extends RuntimeException{
    public EntityException(RecommendationError error) {
        super(error.getMessage());
    }
}
