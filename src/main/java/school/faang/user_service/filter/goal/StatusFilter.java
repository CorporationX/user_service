package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public class StatusFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filterDto) {
        return filterDto.status() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitations, InvitationFilterDto filterDto) {
        return goalInvitations.filter(goalInvitation -> goalInvitation.getStatus() == filterDto.status());
    }
}
