package school.faang.user_service.logging.goal;

public final class GoalMessages {
    public static final String GOAL_COMPLETED_ERROR = "Goal is already completed";
    public static final String MAXIMUM_NUMBER_OF_GOALS_ERROR = "The user has the maximum number of goals";
    public static final String NO_SKILLS_FOUND = "No skills found for provided IDs: {}";
    public static final String GOAL_NOT_FOUND = "Goal not found with id: {}";
    public static final String SUCCESSFULLY_DELETED_GOAL_AND_ALL_ITS_CHILDREN =
            "Successfully deleted goal with id: {} and all its children";
    public static final String NUMBER_OF_ACTIVE_GOALS_REACHED_FOR_A_USER_IN_GOAL_WITH_ID =
            "Validation failed: Maximum number of active goals reached for a user in goal with id: {}";
    public static final String GOAL_IS_ALREADY_COMPLETED_FOR_GOAL_WITH_ID =
            "Validation failed: Goal is already completed for goal with id: {}";

    private GoalMessages() {}
}
