package school.faang.user_service.commonMessages;

public final class ErrorMessages {
    public static final String USER_IS_NULL = "User is null";
    public static final String SAME_ID = "User is trying to subscribe on himself";
    public static final String NEGATIVE_ID = "User can't have negative ID";

    private ErrorMessages() {
        throw new UnsupportedOperationException("This is the util class and cannot be instantiated");
    }
}

