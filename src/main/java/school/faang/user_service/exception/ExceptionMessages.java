package school.faang.user_service.exception;

public final class ExceptionMessages {

    private ExceptionMessages() {}

    public static final String FAILED_PERSISTENCE = "Unable to persist data into database. Please try again.";
    public static final String FAILED_RETRIEVAL = "Unable to retrieve data from database. Please try again.";
    public static final String FAILED_EVENT = "Unable to send an event to message broker.";

    // mentorship
    public static final String SELF_MENTORSHIP = "You cannot mentor yourself.";
    public static final String RECEIVER_NOT_FOUND = "Receiver cannot be found.";
    public static final String REQUESTER_NOT_FOUND = "Requester cannot be found.";
    public static final String MENTORSHIP_FREQUENCY = "You are eligible for only one mentorship within 3 months.";
    public static final String MENTORSHIP_REQUEST_NOT_FOUND = "Mentorship request cannot be found.";
    public static final String MENTORSHIP_ALREADY_ONGOING = "You already mentor this user.";

    // userProfilePic
    public static final String USER_NOT_FOUND = "User with this id %d not in database.";
    public static final String CLOUD_SENDING = "Failed to put request object before sending to cloud.";
    public static final String PICTURE_LOADING_RESTRICTION = "The file exceeds the size of 5 MB.";

    // recommendation
    public static final String RECOMMENDATION_REQUEST_NOT_FOUND = "Request cannot be found.";
    public static final String DISCREPANCY_OF_STATUS = "Status does not match condition.";
    public static final String RECOMMENDATION_FREQUENCY ="Recommendation request can only be sent once every 6 months.";
    public static final String REQUEST_SKILL = "One or more requested skills do not exist in the database";

    // subscription
    public static final String SELF_SUBSCRIPTION = "You cannot follow yourself!";
    public static final String SELF_UNSUBSCRIPTION = "You cannot unfollow yourself!";
    public static final String EXISTING_SUBSCRIPTION = "You are already following this account";
}
