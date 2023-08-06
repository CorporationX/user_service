package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public class InvitationInvitedIdFilter implements InvitationFilter {
    @Override
    public boolean isApplicable(GoalInvitationFilterDto invitationFilterDto) {
        return invitationFilterDto.getInvitedId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, GoalInvitationFilterDto invitationFilterDto) {
        return invitations
                .filter(invitation -> invitation.getInvited().getId() == invitationFilterDto.getInvitedId());
    }
}
