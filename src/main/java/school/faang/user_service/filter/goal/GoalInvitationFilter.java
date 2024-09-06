package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

public interface GoalInvitationFilter {

    boolean isApplicable(InvitationFilterDto filters);

    void apply(List<GoalInvitation> goalInvitations, InvitationFilterDto filters);
}
