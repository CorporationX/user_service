package school.faang.user_service.exception;

public enum ExceptionMessage {
        USER_FOLLOWING_HIMSELF_EXCEPTION("The user cannot follow himself."),
        USER_UNFOLLOWING_HIMSELF_EXCEPTION("The user cannot unfollow himself."),
        REPEATED_SUBSCRIPTION_EXCEPTION("The user cannot follow another user twice."),
        TITLE_EMPTY_EXCEPTION("The title skill is empty"),
        SKILL_ALREADY_EXIST("This skill already added"),
        USER_SKILL_NOT_FOUND("The user does not have this skill");

private final String message;

ExceptionMessage(String message) {
    this.message = message;
}

public String getMessage() {
    return message;
    }
}