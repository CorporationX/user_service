package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

@Component
public class GoalInvitationInvitedNameFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getInvitedNamePattern() != null;
    }

    @Override
    public void apply(List<GoalInvitation> goalInvitations, InvitationFilterDto filters) {
        goalInvitations.removeIf(g -> !g.getInvited().getUsername().contains(filters.getInviterNamePattern()));
    }
}
