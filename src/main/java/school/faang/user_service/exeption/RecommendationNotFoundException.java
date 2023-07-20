package school.faang.user_service.exeption;

public class RecommendationNotFoundException extends RuntimeException {
    public RecommendationNotFoundException(String message) {
        super(message);
    }
}
