package school.faang.user_service.exception;

public class RecommendationRequestNotFoundException extends RuntimeException {
    public RecommendationRequestNotFoundException(String message) {
        super(message);
    }
}
