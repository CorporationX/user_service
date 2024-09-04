package school.faang.user_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessages {
    USER_NOT_FOUND("User with id %d not found"),
    SUBSCRIBE_ITSELF_VALIDATION("User can't subscribe on himself"),
    SUBSCRIPTION_NOT_FOUND("User with id %d not subscribed on user with id %d"),
    SUBSCRIPTION_ALREADY_EXIST("User with id %d already subscribed to user with id %d");

    private final String message;
}
