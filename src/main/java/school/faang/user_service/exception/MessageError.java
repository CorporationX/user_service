package school.faang.user_service.exception;

public enum MessageError {
    SUBSCRIBE_TO_YOURSELF_EXCEPTION("You can't subscribe/unsubscribe to yourself"),
    SUBSCRIPTION_NOT_AND_ALREADY_EXISTS_EXCEPTION("You are already subscribed/unsubscribed to this user"),
    USER_NOT_FOUND_EXCEPTION("User by ID is not found");

    private final String message;

    MessageError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
