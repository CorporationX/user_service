package school.faang.user_service.exception.recommendation;

public class DataValidationException extends RuntimeException {
    public DataValidationException(RecommendationError error) {
        super(error.getMessage());
    }
}