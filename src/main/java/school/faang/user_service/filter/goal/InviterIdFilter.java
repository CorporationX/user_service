package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.Objects;
import java.util.stream.Stream;

public class InviterIdFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filterDto) {
        return filterDto.invitedId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitations, InvitationFilterDto filterDto) {
        return goalInvitations.filter(goalInvitation ->
                Objects.equals(goalInvitation.getInviter().getId(), filterDto.inviterId()));
    }
}
