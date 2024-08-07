package school.faang.user_service.exception;

public enum ExceptionMessage {

    IMPOSSIBLE_REJECTION(
            "It is impossible to cancel the request because it is already in the state: %s."
    ),
    RECOMMENDATION_COOLDOWN_NOT_EXCEEDED(
            "It is not possible to request a recommendation before the specified time threshold: %s."
    ),
    BLANK_RECOMMENDATION_MESSAGE(
            "Cannot send recommendations with empty message."
    ),
    USER_DOES_NOT_EXIST(
            "User with id %s does not exist."
    ),
    SKILLS_DONT_EXIST(
            "The specified skills or they almost do not exist"
    )
    ;

    private final String msg;

    ExceptionMessage(String message) {
        msg = message;
    }

    public String getMessage() {
        return msg;
    }
}
