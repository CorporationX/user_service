package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface InvitationFilter {
    boolean isApplicable(GoalInvitationFilterDto invitationFilterDto);

    Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, GoalInvitationFilterDto invitationFilterDto);
}
