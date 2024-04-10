package school.faang.user_service.service.goal.filter_goalinvitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.filter.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Component
public class GoalInviterNamePatternFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(GoalInvitationFilterDto filters) {
        return filters.getInviterNamePattern() != null;
    }

    @Override
    public boolean apply(GoalInvitation goalInvitation, GoalInvitationFilterDto filters) {
        return goalInvitation.getInviter().getUsername().equals(filters.getInviterNamePattern());
    }
}