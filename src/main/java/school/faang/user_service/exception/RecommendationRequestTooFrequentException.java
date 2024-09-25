package school.faang.user_service.exception;

public class RecommendationRequestTooFrequentException extends RuntimeException {
    public RecommendationRequestTooFrequentException(String message) {
        super(message);
    }
}
