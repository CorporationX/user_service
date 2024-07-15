package school.faang.user_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorMessage {
    USER_DOES_NOT_EXIST("User does not exist"),
    REQUEST_TO_YOURSELF("You can't send a mentorship request to yourself"),
    EARLY_REQUEST("You can't send a mentorship request now. Less than 3 months have passed since your last request"),
    REQUEST_DOES_NOT_EXIST("Request does not exist"),
    ALREADY_MENTOR("You are already the requester's mentor");

    private final String message;
}
