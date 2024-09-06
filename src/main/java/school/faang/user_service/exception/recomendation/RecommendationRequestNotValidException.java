package school.faang.user_service.exception.recomendation;

public class RecommendationRequestNotValidException extends RuntimeException {
    public RecommendationRequestNotValidException(String message) {
        super(message);
    }
}
