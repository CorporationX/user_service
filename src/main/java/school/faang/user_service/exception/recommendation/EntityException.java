package school.faang.user_service.exception.recommendation;

public class EntityException extends RuntimeException {
    public EntityException(RecommendationError error, Long details) {
        super(String.format("%s: %d", error.getMessage(), details));
    }
}
