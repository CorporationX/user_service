package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.filter_dto.InvitationFilterDto;
import school.faang.user_service.model.entity.GoalInvitation;

import java.util.stream.Stream;

@Component
public class GoalInvitationRequestStatusFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitations, InvitationFilterDto filters) {
        return goalInvitations.filter(g -> g.getStatus() == filters.getStatus());
    }
}
