package school.faang.user_service.exception;

public class RecommendationRequestValidationException extends RuntimeException {
    public RecommendationRequestValidationException(String message) {
        super(message);
    }
}
