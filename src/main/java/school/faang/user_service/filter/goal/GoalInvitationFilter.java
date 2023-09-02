package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface GoalInvitationFilter {
    boolean isApplicable(GoalInvitationFilterDto filter);
    Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, GoalInvitationFilterDto filter);
}
