package school.faang.user_service.commonMessages;

import java.text.MessageFormat;

public final class ErrorMessagesForEvent {
    public static final String USER_ID_IS_NULL = "User ID is null";
    public static final String EVENT_ID_IS_NULL = "Event ID is null";
    public static final MessageFormat USER_IS_ALREADY_REGISTERED = new MessageFormat(
            "The userId:{1} has already been registered for the eventId:{0}");

    public static final MessageFormat USER_IS_NOT_REGISTERED = new MessageFormat(
            "The userId:{1} is not registered for the eventId:{0}");

    public static final String NEGATIVE_USER_ID = "User can't have negative ID";
    public static final String NEGATIVE_EVENT_ID = "Event can't have negative ID";

    private ErrorMessagesForEvent() {
        throw new UnsupportedOperationException("This is the util class and cannot be instantiated");
    }
}
