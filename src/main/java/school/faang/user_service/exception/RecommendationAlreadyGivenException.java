package school.faang.user_service.exception;

public class RecommendationAlreadyGivenException extends RuntimeException {
    public RecommendationAlreadyGivenException(String message) {
        super(message);
    }
}
