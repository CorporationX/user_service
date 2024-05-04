package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class GoalInvitationStatusFilter implements GoalInvitationFilter {

    @Override
    public boolean isAcceptable(InvitationFilterDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public Stream<GoalInvitation> applyFilter(Stream<GoalInvitation> invitations, InvitationFilterDto filterDto) {
        return invitations.filter(invitation -> invitation.getStatus().equals(filterDto.getStatus()));
    }
}
