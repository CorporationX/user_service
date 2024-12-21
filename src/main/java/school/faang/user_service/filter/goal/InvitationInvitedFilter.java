package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public class InvitationInvitedFilter implements InvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.getInvitedId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filter) {
        return invitations.filter(invitation -> invitation.getInvited().getId().equals(filter.getInvitedId()));
    }
}
