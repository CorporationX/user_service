package school.faang.user_service.service.goal.filter;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface InvitationFilter {
    boolean isApplicable(InvitationFilterDto filters);

    Stream<GoalInvitation> apply(GoalInvitation invitation, InvitationFilterDto filters);
}
