package school.faang.user_service.service.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GoalInvitationUtil {
    public final int MAX_ACTIVE_GOALS = 3;
    public final String GOAL_MISSING = "Goal wasn't found";
    public final String INVITATION_MISSING = "Invitation wasn't found";
    public final String INVITER_INVITED_SAME = "Inviter and invited are the same user";
    public final String INVITER_MISSING = "Inviter  not found";
    public final String INVITED_MISSING = "Invited not found";
    public final String INVITED_ALREADY_WORKING_ON_GOAL = "Invited user is already working on this goal";
    public final String INVITED_MAX_ACTIVE_GOALS = "Invited user reached max active goals: " + MAX_ACTIVE_GOALS;
}
