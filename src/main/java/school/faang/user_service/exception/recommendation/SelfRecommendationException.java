package school.faang.user_service.exception.recommendation;

public class SelfRecommendationException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "User cannot leave recommendation for themself!";

    public SelfRecommendationException(String message) {
        super(message);
    }

    public SelfRecommendationException() {
        super(DEFAULT_MESSAGE);
    }
}
