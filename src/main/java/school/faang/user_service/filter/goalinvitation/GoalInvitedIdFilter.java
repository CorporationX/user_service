package school.faang.user_service.filter.goalinvitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Component
public class GoalInvitedIdFilter implements GoalInvitationFilter {

    @Override
    public boolean isApplicable(GoalInvitationFilterDto filters) {
        return filters.getInvitedId() != null;
    }

    @Override
    public boolean apply(GoalInvitation goalInvitation, GoalInvitationFilterDto filters) {
        return goalInvitation.getInvited().getId() == filters.getInvitedId();
    }
}