package school.faang.user_service.filter.invitation;

import school.faang.user_service.dto.goal.InvitationFilterIDto;
import school.faang.user_service.entity.goal.GoalInvitation;

public interface InvitationFilter {
    boolean doFilter(GoalInvitation goalInvitation, InvitationFilterIDto filterDto);

    boolean isApplicable(InvitationFilterIDto criteria);
}
