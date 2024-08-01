package school.faang.user_service.exception;

public final class ExceptionMessages {

    private ExceptionMessages() {}

    public static final String FAILED_PERSISTENCE = "Unable to persist data into database. Please try again.";
    public static final String FAILED_RETRIEVAL = "Unable to retrieve data from database. Please try again.";

    // mentorship
    public static final String SELF_MENTORSHIP = "You cannot mentor yourself.";
    public static final String RECEIVER_NOT_FOUND = "Receiver cannot be found.";
    public static final String REQUESTER_NOT_FOUND = "Requester cannot be found.";
    public static final String MENTORSHIP_FREQUENCY = "You are eligible for only one mentorship within 3 months.";
    public static final String MENTORSHIP_REQUEST_NOT_FOUND = "Mentorship request cannot be found.";
    public static final String MENTORSHIP_ALREADY_ONGOING = "You already mentor this user.";

    // recommendation
    public static final String RECOMMENDATION_REQUEST_NOT_FOUND = "Request cannot be found.";
    public static final String DISCREPANCY_OF_STATUS = "Status does not match condition.";
    public static final String RECOMMENDATION_FREQUENCY ="Recommendation request can only be sent once every 6 months.";
    public static final String REQUEST_SKILL = "One or more requested skills do not exist in the database";

    // premium
    public static final String CLOSURE_THREAD_EXCEPTION = "Не удалось завершить выполнение задач";
}
