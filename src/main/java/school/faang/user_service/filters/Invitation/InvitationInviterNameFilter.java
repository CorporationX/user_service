package school.faang.user_service.filters.Invitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InvitationInviterNameFilter implements InvitationFilter {
    @Override
    public boolean isAcceptable(InvitationFilterDto goal) {
        return !goal.getInviterNamePattern().isEmpty() && !goal.getInviterNamePattern().isBlank();
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goal, InvitationFilterDto filters) {
        return goal.filter(invintation -> invintation.getInviter().getUsername().matches(filters.getInviterNamePattern()));
    }
}
