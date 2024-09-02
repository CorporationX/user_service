package school.faang.user_service.service.goal.util;

public class GoalInvitationErrorMessages {
    public static final String USERS_SAME_MESSAGE_FORMAT = "Inviter user id: %s and invited user id: %s should not be same";
    public static final String GOAL_NOT_FOUND_MESSAGE_FORMAT = "Goal with id: %s not found";
    public static final String INVITER_NOT_FOUND_MESSAGE_FORMAT = "Inviter id: %s not found";
    public static final String INVITED_USER_NOT_FOUND_MESSAGE_FORMAT = "Invited user id: %s not found";
    public static final String GOAL_INVITATION_NOT_FOUND = "Goal invitation with id: %s not found";

    public static final String USER_GOALS_LIMIT_EXCEEDED = "Number of goals in User with id: %s exceeds the allowed limit of: %s";
    public static final String USER_ALREADY_HAS_GOAL = "User with id: %s already has goal with id: %s";
}
