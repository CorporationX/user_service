package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public class GoalInvitationInvitedFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(GoalInvitationFilterDto filter) {
        return filter.getInvitedId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, GoalInvitationFilterDto filter) {
        return invitations.filter(invitation -> invitation.getInvited().getId() == filter.getInvitedId());
    }
}
