package school.faang.user_service.messages;

public class ErrorMessages {
    public static final String USER_NOT_FOUND_ERROR = "No user with ID %d\n";
    private static final  String ERROR_NOT_FOUND_CONTACT = "User contact preference not found for userId: %d\n";

    public static String getErrorNotFoundContact(long userId) {
        return String.format(ERROR_NOT_FOUND_CONTACT, userId);
    }
}
