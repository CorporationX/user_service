package school.faang.user_service.exception.recommendation;

public class RecommendationCooldownException extends RuntimeException {
    public RecommendationCooldownException(String message) {
        super(message);
    }
}
