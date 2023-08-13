package school.faang.user_service.filter.goalinvitation;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface GoalInvitationFilter {
    boolean isApplicable(InvitationFilterDto filterDto);

    Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitationStream, InvitationFilterDto filterDto);
}
