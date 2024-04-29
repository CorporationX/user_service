package school.faang.user_service.exceptions;

public enum ExceptionMessage {
    USER_FOLLOWING_HIMSELF_EXCEPTION("The user cannot follow himself."),
    REPEATED_SUBSCRIPTION_EXCEPTION("The user cannot follow another user twice.");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
