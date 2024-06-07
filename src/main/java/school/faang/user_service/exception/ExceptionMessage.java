package school.faang.user_service.exception;

public enum ExceptionMessage {
    NULL_OR_BLANK_EVENT_TITLE_EXCEPTION("The event cannot have null-valued or blank title."),
    INVALID_EVENT_START_DATE_EXCEPTION("The event start date must be non null and cannot be in the past."),
    INVALID_EVENT_END_DATE_EXCEPTION("The event end date cannot be earlier than the start date."),
    NULL_EVENT_OWNER_ID_EXCEPTION("The event must have owner."),
    NO_SUCH_USER_EXCEPTION("No such user detected in system for passed owner id."),
    INAPPROPRIATE_OWNER_SKILLS_EXCEPTION("The owner has inappropriate skills to create/update such event."),
    NULL_EVENT_FILTER_EXCEPTION("The event filter must be non null."),
    NO_SUCH_EVENT_EXCEPTION("Database doesn't contains event with such id."),
    USER_FOLLOWING_HIMSELF_EXCEPTION("The user cannot follow himself."),
    USER_UNFOLLOWING_HIMSELF_EXCEPTION("The user cannot unfollow himself."),
    REPEATED_SUBSCRIPTION_EXCEPTION("The user cannot follow another user twice."),
    NO_FILE_IN_REQUEST("There is no file send");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}