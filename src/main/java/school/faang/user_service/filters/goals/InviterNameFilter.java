package school.faang.user_service.filters.goals;

import school.faang.user_service.dto.InvitationFilterIDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filters.interfaces.GoalsFilter;

import java.util.stream.Stream;

public class InviterNameFilter implements GoalsFilter {
    @Override
    public boolean isAcceptable(InvitationFilterIDto invitationFilterIDto) {
        return invitationFilterIDto.inviterNamePattern() != null;
    }

    @Override
    public Stream<GoalInvitation> accept(Stream<GoalInvitation> invitations, InvitationFilterIDto invitationFilterIDto) {
        return invitations.filter(invitation -> matchesPattern(invitationFilterIDto.inviterNamePattern(), invitation.getInviter().getUsername()));
    }

    private boolean matchesPattern(String pattern, String value) {
        return pattern == null || value.matches(pattern);
    }
}
