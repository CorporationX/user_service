package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public class GoalInvitationInviterFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(GoalInvitationFilterDto filter) {
        return filter.getInviterId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, GoalInvitationFilterDto filter) {
        return invitations.filter(invitation -> invitation.getInviter().getId() == filter.getInviterId());
    }
}
