package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface InvitationFilter {
    boolean isApplicable(InvitationFilterDto filter);
    Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filter);
}
