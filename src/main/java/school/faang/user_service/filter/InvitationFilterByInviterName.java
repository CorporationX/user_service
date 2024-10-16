package school.faang.user_service.filter;

import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public class InvitationFilterByInviterName implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(GoalInvitationFilterDto invitationFilterDto) {
        return invitationFilterDto.getInviterNamePattern() == null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitationStream, GoalInvitationFilterDto invitationFilterDto) {
        return invitationStream.filter(invitation -> invitation.getInviter().getUsername().contains(invitationFilterDto.getInviterNamePattern()));
    }
}
