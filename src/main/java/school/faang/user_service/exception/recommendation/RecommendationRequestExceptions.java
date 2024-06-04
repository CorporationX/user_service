package school.faang.user_service.exception.recommendation;

import lombok.Getter;

@Getter
public enum RecommendationRequestExceptions {
    REQUEST_MESSAGE_EMPTY("Request message is empty"),
    REQUEST_REQUESTER_ID_EMPTY("Recommendation request requester ID is empty"),
    REQUEST_RECEIVER_ID_EMPTY("Recommendation request requester ID is empty"),
    REQUEST_SKILLS_EMPTY("Recommendation request skills requests are empty"),
    REQUEST_EXPIRATION_TIME_NOT_PASSED("You can't send another recommendation request. 6 months haven't passed"),
    REJECT_REQUEST_STATUS_NOT_VALID("You can't reject accepted or rejected request"),
    REQUEST_NOT_FOUND("Recommendation request not found");

    private final String message;

    RecommendationRequestExceptions(String message) {
        this.message = message;
    }
}
