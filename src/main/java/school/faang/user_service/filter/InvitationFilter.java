package school.faang.user_service.filter;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

public interface InvitationFilter {

    boolean isApplicable(InvitationFilterDto filters);

    List<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filters);
}
