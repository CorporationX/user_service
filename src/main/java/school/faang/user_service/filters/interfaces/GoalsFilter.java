package school.faang.user_service.filters.interfaces;

import school.faang.user_service.dto.InvitationFilterIDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface GoalsFilter {
    boolean isAcceptable(InvitationFilterIDto invitationFilterIDto);
    Stream<GoalInvitation> accept(Stream<GoalInvitation> invitations, InvitationFilterIDto invitationFilterIDto);
}
