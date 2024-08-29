package school.faang.user_service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InvitationInviterIdNameFilter implements InvitationFilter {
    @Override
    public boolean isAcceptable(InvitationFilterDto goal) {
        return goal.getInviterId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goal, InvitationFilterDto filters) {
        return goal.filter(invintation -> invintation.getInviter().getId() == (filters.getInviterId()));
    }
}
