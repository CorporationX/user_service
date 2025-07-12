package school.faang.user_service.exception.recommendation;

import school.faang.user_service.exception.ForbiddenException;

public class SelfRecommendationException extends ForbiddenException {
    private static final String DEFAULT_MESSAGE = "User cannot leave recommendation for themself!";

    public SelfRecommendationException(String message) {
        super(message);
    }

    public SelfRecommendationException() {
        super(DEFAULT_MESSAGE);
    }
}
