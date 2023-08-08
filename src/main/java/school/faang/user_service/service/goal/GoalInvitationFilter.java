package school.faang.user_service.service.goal;

import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.dto.filter.GoalInvitationFilterIDto;

import java.util.stream.Stream;

public interface GoalInvitationFilter {

    boolean isApplicable(GoalInvitationFilterIDto filters);

    Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, GoalInvitationFilterIDto filters);
}
