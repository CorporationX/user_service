package school.faang.user_service.exception;

import school.faang.user_service.exception.recommendation.RecommendationError;

public class DataValidationException extends RuntimeException {
    private final String error;
    public DataValidationException(RecommendationError error) {
        super(error.getMessage());
        this.error = error.getMessage();
    }
}

