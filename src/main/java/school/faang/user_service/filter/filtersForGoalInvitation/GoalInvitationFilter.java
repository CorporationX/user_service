package school.faang.user_service.filter.filtersForGoalInvitation;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

public interface GoalInvitationFilter {
    boolean isApplicable(InvitationFilterDto filterDto);
    List<GoalInvitation> apply(Stream<GoalInvitation> goalInvitationStream, InvitationFilterDto filterDto);
}
