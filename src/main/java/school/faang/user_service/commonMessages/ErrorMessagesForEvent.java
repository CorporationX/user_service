package school.faang.user_service.commonMessages;


public final class ErrorMessagesForEvent {
    public static final String USER_ID_IS_NULL = "User ID is null";
    public static final String EVENT_ID_IS_NULL = "Event ID is null";
    public static final String USER_IS_ALREADY_REGISTERED_FORMAT =
            "The userId:{0} has already been registered for the eventId:{1}";

    public static final String USER_IS_NOT_REGISTERED_FORMAT =
            "The userId:{0} is not registered for the eventId:{1}";

    public static final String NEGATIVE_USER_ID = "User can't have negative ID";
    public static final String NEGATIVE_EVENT_ID = "Event can't have negative ID";

    private ErrorMessagesForEvent() {
        throw new UnsupportedOperationException("This is the util class and cannot be instantiated");
    }
}
