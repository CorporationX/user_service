package school.faang.user_service.exception;

public enum MessageForGoalInvitationService {
    INVITER_ID_IS_NULL("InviterId can not be null."),
    INVITED_USER_ID_IS_NULL("InvitedUserId cannot be null."),
    NO_GOAL_IN_DB("Database hasn't goal."),
    INVITER_ID_EQUALS_INVITED_USER_ID("InviterId cannot be the same as InvitedUserId."),
    INPUT_IS_NULL("Input should not be null."),
    NO_GOAL_INVITATION_IN_DB("Database hasn't goalInvitation."),
    INVITED_HAS_GOAL("Invited should not has such goal."),
    MORE_THEN_THREE_GOALS("GoalInvitation should has less then 3 goals");

    private final String message;

    MessageForGoalInvitationService(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
