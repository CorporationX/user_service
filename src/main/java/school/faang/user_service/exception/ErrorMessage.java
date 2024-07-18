package school.faang.user_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorMessage {
    MENTOR_NOT_FOUND("Mentor not found"),
    MENTEE_NOT_FOUND("Mentee not found"),
    USER_NOT_FOUND("User not found"),;

    private final String message;
}
