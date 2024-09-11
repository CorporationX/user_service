package school.faang.user_service.service.goal.filter.invitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InviterNamePatternFilter implements InvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.inviterNamePattern() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filters) {
        return invitations.filter(invitation -> {
            String username = invitation.getInviter().getUsername().toLowerCase();
            String pattern = filters.inviterNamePattern().toLowerCase();
            return username.contains(pattern);
        });
    }
}
