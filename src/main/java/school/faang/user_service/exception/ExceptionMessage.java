package school.faang.user_service.exception;

public enum ExceptionMessage {
    NULL_OR_BLANK_EVENT_TITLE_EXCEPTION("The event cannot have null-valued or blank title."),
    INVALID_EVENT_START_DATE_EXCEPTION("The event start date must be non null and cannot be in the past."),
    INVALID_EVENT_END_DATE_EXCEPTION("The event end date cannot be earlier than the start date."),
    NULL_EVENT_OWNER_ID_EXCEPTION("The event must have owner."),
    NO_SUCH_USER_EXCEPTION("No such user detected in system for passed user id."),
    INAPPROPRIATE_OWNER_SKILLS_EXCEPTION("The owner has inappropriate skills to create/update such event."),
    NULL_EVENT_FILTER_EXCEPTION("The event filter must be non null."),
    NO_SUCH_EVENT_EXCEPTION("Database doesn't contains event with such id."),
    USER_FOLLOWING_HIMSELF_EXCEPTION("The user cannot follow himself."),
    USER_UNFOLLOWING_HIMSELF_EXCEPTION("The user cannot unfollow himself."),
    REPEATED_SUBSCRIPTION_EXCEPTION("The user cannot follow another user twice."),
    FILE_SIZE_EXCEPTION("The uploaded file can have a maximum size of no more than 5 MB."),
    PICTURE_TYPE_EXCEPTION("Only image could be uploaded as user picture."),
    FILE_PROCESSING_EXCEPTION("During file processing was caught exception: "),
    USER_AVATAR_ABSENCE_EXCEPTION("User doesn't have avatar.");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}