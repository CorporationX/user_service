package school.faang.user_service.filters;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface InvitationFilter {
    boolean isAcceptable(InvitationFilterDto goal);

    Stream<GoalInvitation> apply(Stream<GoalInvitation> goal, InvitationFilterDto filters);

}
