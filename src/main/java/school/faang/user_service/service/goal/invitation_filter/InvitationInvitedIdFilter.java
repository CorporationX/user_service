package school.faang.user_service.service.goal.invitation_filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InvitationInvitedIdFilter implements InvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto invitationFilterDto) {
        return invitationFilterDto.getInvitedId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto invitationFilterDto) {
        return invitations.filter(goalInvitation -> goalInvitation.getInvited().getId().equals(invitationFilterDto.getInvitedId()));
    }
}
