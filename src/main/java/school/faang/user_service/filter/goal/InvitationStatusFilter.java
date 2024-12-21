package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public class InvitationStatusFilter implements InvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filter) {
        return invitations.filter(invitation -> invitation.getStatus() == filter.getStatus());
    }
}
