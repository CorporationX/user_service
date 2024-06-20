package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class RequestStatusFilter implements InvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(GoalInvitation invitation, InvitationFilterDto filters) {
        return Stream.of(invitation).filter(goalInvitation -> goalInvitation.getStatus() == filters.getStatus());
    }
}
