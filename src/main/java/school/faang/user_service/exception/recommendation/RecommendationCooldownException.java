package school.faang.user_service.exception.recommendation;

import school.faang.user_service.exception.DataValidationException;

public class RecommendationCooldownException extends DataValidationException {
    public RecommendationCooldownException(String message) {
        super(message);
    }
}
