package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class GoalInvitationInviterNameFilter implements GoalInvitationFilter {

    @Override
    public boolean isAcceptable(InvitationFilterDto filterDto) {
        return filterDto.getInviterNamePattern() != null;
    }

    @Override
    public Stream<GoalInvitation> applyFilter(Stream<GoalInvitation> invitations, InvitationFilterDto filterDto) {
        return invitations.filter(invitation -> invitation.getInviter().getUsername().startsWith(filterDto.getInviterNamePattern()));
    }
}
