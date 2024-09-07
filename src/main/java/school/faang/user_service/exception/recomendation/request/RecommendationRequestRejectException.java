package school.faang.user_service.exception.recomendation.request;

import org.springframework.http.HttpStatus;

public class RecommendationRequestRejectException extends RecommendationRequestException {
    private final static String MESSAGE = "Recommendation request must have a status PENDING";

    public RecommendationRequestRejectException() {
        super(MESSAGE, HttpStatus.CONFLICT);
    }
}
