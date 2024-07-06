package school.faang.user_service.exception.message;

public enum ExceptionMessage {
    INVALID_EVENT_DATES_EXCEPTION("The event start date cannot be after end date."),
    NULL_EVENT_ID_EXCEPTION("The event id cannot be null during update."),
    NO_SUCH_USER_EXCEPTION("No such user detected in system for passed user id."),
    INAPPROPRIATE_OWNER_SKILLS_EXCEPTION("The owner has inappropriate skills to create/update such event."),
    NO_SUCH_EVENT_EXCEPTION("Database doesn't contain event with such id."),
    USER_FOLLOWING_HIMSELF_EXCEPTION("The user cannot follow himself."),
    USER_UNFOLLOWING_HIMSELF_EXCEPTION("The user cannot unfollow himself."),
    REPEATED_SUBSCRIPTION_EXCEPTION("The user cannot follow another user twice."),
    AVATAR_FILE_SIZE_EXCEPTION("The uploaded avatar file can have a maximum size of no more than 5 MB."),
    PICTURE_TYPE_EXCEPTION("Only image could be uploaded as user picture."),
    FILE_PROCESSING_EXCEPTION("During file processing was caught exception: "),
    USER_AVATAR_ABSENCE_EXCEPTION("User doesn't have avatar."),
    REPEATED_USER_CREATION_EXCEPTION("User with such id already exists in system."),
    RANDOM_AVATAR_GETTING_EXCEPTION("Getting random avatar failed."),
    NO_SUCH_COUNTRY_EXCEPTION("Database doesn't contain country with such id."),
    REQUESTER_ID_EQUALS_RECEIVER_ID("Requester can not be the same as receiver"),
    REQUEST_ALREADY_HAS_STATUS("To get new status, status should be null"),
    NO_REQUEST_IN_DB("No such request in database"),
    USER_ALREADY_HAS_SUCH_MENTOR("User can not send request to mentor twice"),
    TOO_MUCH_REQUESTS("Requests are made ones per 3 months"),
      NO_FILE_IN_REQUEST("There is no file send"),
    INPUT_OUTPUT_EXCEPTION("Something wrong with input or output"),
    INPUT_IS_EMPTY("Input should not be empty");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}