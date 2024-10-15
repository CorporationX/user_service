package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.goal.InvitationFilterDto;
import school.faang.user_service.model.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class GoalInvitationInviterNameFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.inviterNamePattern() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitations, InvitationFilterDto filter) {
        return goalInvitations.filter(
                goalInvitation -> goalInvitation.getInviter().getUsername().equals(filter.inviterNamePattern())
        );
    }
}