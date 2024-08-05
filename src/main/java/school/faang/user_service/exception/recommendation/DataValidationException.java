package school.faang.user_service.exception.recommendation;

public class DataValidationException extends RuntimeException {
    public DataValidationException(RecommendationError error, String details) {
        super(String.format("%s: %s", error.getMessage(), details));
    }
}