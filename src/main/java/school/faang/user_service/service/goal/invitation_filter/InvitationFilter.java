package school.faang.user_service.service.goal.invitation_filter;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface InvitationFilter {
    boolean isApplicable(InvitationFilterDto invitationFilterDto);

    Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto invitationFilterDto);
}
