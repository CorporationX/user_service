package school.faang.user_service.filters.goals;

import school.faang.user_service.dto.InvitationFilterIDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filters.interfaces.GoalsFilter;

import java.util.stream.Stream;

public class InvitedIdFilter implements GoalsFilter {
    @Override
    public boolean isAcceptable(InvitationFilterIDto invitationFilterIDto) {
        return invitationFilterIDto.invitedId() != null;
    }

    @Override
    public Stream<GoalInvitation> accept(Stream<GoalInvitation> invitations, InvitationFilterIDto invitationFilterIDto) {
        return invitations.filter(invitation -> invitationFilterIDto.invitedId().equals(invitation.getInvited().getId()));
    }
}
