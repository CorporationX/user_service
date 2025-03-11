package school.faang.user_service.message.mentorship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionMessage {
    INVALID_ID("Invalid ID: ID not be less than 1"),
    USER_NOT_FOUND("User not found"),
    EQUAL_IDS("The transmitted user IDs are equal");

    private final String message;
}
