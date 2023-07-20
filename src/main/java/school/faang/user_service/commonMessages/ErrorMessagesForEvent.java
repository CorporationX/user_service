package school.faang.user_service.commonMessages;

import java.text.MessageFormat;

public final class ErrorMessagesForEvent {
    public static final String INPUT_DATA_IS_NULL = "Input data is null";
    public static final MessageFormat USER_IS_ALREADY_REGISTERED = new MessageFormat(
            "The userId:{0} has already been registered for the eventId:{1}");
    public static final String NEGATIVE_ID = "User can't have negative ID";

    private ErrorMessagesForEvent() {
        throw new UnsupportedOperationException("This is the util class and cannot be instantiated");
    }
}
