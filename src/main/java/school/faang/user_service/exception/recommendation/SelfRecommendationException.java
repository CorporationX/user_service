package school.faang.user_service.exception.recommendation;

import school.faang.user_service.exception.ForbiddenException;

public class SelfRecommendationException extends ForbiddenException {
    public SelfRecommendationException(String message) {
        super(message);
    }
}
