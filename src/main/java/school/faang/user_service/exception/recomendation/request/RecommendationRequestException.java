package school.faang.user_service.exception.recomendation.request;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

public class RecommendationRequestException extends RuntimeException {
    @Getter
    private HttpStatusCode httpStatus;

    public RecommendationRequestException(String message, HttpStatusCode httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
