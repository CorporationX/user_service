package school.faang.user_service.message.mentorship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionMessage {
    INVALID_ID("ID not be less than 1"),
    USER_NOT_FOUND("User with ID %d not found"),
    EQUAL_IDS("The transmitted user IDs are equal"),
    NO_USER_MENTEE("User does not have such a mentee"),
    NO_USER_MENTOR("User does not have such a mentor"),
    REGISTER_EXCEPTION("Request URI: {}\nHTTP Status: {}\nException: {} - {}");

    private final String message;
}
