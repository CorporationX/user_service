package school.faang.user_service.exception;

public enum ExceptionMessage {
    NULL_OR_BLANK_EVENT_TITLE_EXCEPTION("The event cannot have null-valued or blank title."),
    INVALID_EVENT_START_DATE_EXCEPTION("The event must be non null and cannot be in the past."),
    NULL_EVENT_OWNER_ID_EXCEPTION("The event must have owner."),
    INAPPROPRIATE_OWNER_SKILLS_EXCEPTION("The owner has inappropriate skills to create such event.");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}