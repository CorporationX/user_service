package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public class InvitationInviterIdFilter implements InvitationFilter {
    @Override
    public boolean isApplicable(GoalInvitationFilterDto invitationFilterDto) {
        return invitationFilterDto.getInviterId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, GoalInvitationFilterDto invitationFilterDto) {
        return invitations
                .filter(invitation -> invitation.getInviter().getId() == invitationFilterDto.getInviterId());
    }
}
