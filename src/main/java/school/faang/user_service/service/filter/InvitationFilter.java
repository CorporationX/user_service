package school.faang.user_service.service.filter;

import school.faang.user_service.entity.goal.GoalInvitation;

public interface InvitationFilter {
    boolean filter(GoalInvitation goalInvitation);
}
