package school.faang.user_service.service.goal.filter_goalinvitation;

import school.faang.user_service.dto.goal.filter.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

public interface GoalInvitationFilter {
    boolean isApplicable(GoalInvitationFilterDto filters);
    boolean apply(GoalInvitation goalInvitation, GoalInvitationFilterDto filters);
}