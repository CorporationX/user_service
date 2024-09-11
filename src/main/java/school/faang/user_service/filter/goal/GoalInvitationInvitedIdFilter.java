package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.Objects;

@Component
public class GoalInvitationInvitedIdFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getInvitedId() != null;
    }

    @Override
    public void apply(List<GoalInvitation> goalInvitations, InvitationFilterDto filters) {
        goalInvitations.removeIf(g -> !Objects.equals(g.getInvited().getId(), filters.getInvitedId()));
    }
}
