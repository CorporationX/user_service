package school.faang.user_service.exception.recomendation.request;

import org.springframework.http.HttpStatus;

public class RecommendationRequestNotFoundException extends RecommendationRequestException {
    private final static String MESSAGE = "Recommendation request not found";

    public RecommendationRequestNotFoundException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
