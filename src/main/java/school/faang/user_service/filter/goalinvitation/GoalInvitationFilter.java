package school.faang.user_service.filter.goalinvitation;

import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

public interface GoalInvitationFilter {
    boolean isApplicable(GoalInvitationFilterDto filters);
    boolean apply(GoalInvitation goalInvitation, GoalInvitationFilterDto filters);
}