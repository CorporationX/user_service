package school.faang.user_service.exception;

public class RejectRecommendationException extends RuntimeException {
    public RejectRecommendationException(ExceptionMessage errorMessage, Object... arguments) {
        super(String.format(errorMessage.getMessage(), arguments));
    }

    public RejectRecommendationException(String foramtString, Object... arguments) {
        super(String.format(foramtString, arguments));
    }

    public RejectRecommendationException(String message) {
        super(message);
    }
}
