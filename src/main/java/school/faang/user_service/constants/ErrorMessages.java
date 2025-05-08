package school.faang.user_service.constants;

public class ErrorMessages {
    public static final String ERROR_NULL_MENTORSHIP_REQUEST_DTO = "MentorshipRequestDto can't be null.";
    public static final String ERROR_NULL_REJECTION_DTO = "RejectionDto can't be null.";
    public static final String ERROR_SELF_REQUEST = "You cannot request mentorship from yourself.";
    public static final String ERROR_NULL_REQUEST_DTO = "RequestFilterDto can't be null.";
    public static final String ERROR_ALREADY_MENTOR = "User is already a mentor for the requester.";
    public static final String ERROR_EMPTY_REJECTION = "Rejection reason cannot be empty.";

    private static final String ERROR_SHORT_DESCRIPTION = "Description should be at least %d characters long.\n";
    private static final String ERROR_TOO_FREQUENT_REQUESTS = "You can only request mentorship once every %d months.\n";
    private static final String ERROR_USER_NOT_FOUND = "User with the given ID(s): %s was not found.\n";
    private static final String ERROR_ABSENT_REQUEST = "The request %d was not found.\n";

    public static String getShortDescriptionError(int minLength) {
        return String.format(ERROR_SHORT_DESCRIPTION, minLength);
    }

    public static String getFrequentRequestError(int months) {
        return String.format(ERROR_TOO_FREQUENT_REQUESTS, months);
    }

    public static String getUserNotFoundError(String userIds) {
        return String.format(ERROR_USER_NOT_FOUND, userIds);
    }

    public static String getAbsentRequestError(long requestId) {
        return String.format(ERROR_ABSENT_REQUEST, requestId);
    }
}
