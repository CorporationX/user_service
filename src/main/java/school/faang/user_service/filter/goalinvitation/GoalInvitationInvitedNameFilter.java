package school.faang.user_service.filter.goalinvitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class GoalInvitationInvitedNameFilter implements GoalInvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto filterDto) {
        return filterDto.invitedNamePattern() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitationStream, InvitationFilterDto filterDto) {
        return goalInvitationStream.filter(
            goalInvitation -> goalInvitation.getInvited().getUsername().contains(filterDto.invitedNamePattern())
        );
    }
}
