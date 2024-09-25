package school.faang.user_service.exception;

public class RecommendationRequestAlreadyProcessedException extends RuntimeException {
    public RecommendationRequestAlreadyProcessedException(String message) {
        super(message);
    }
}
