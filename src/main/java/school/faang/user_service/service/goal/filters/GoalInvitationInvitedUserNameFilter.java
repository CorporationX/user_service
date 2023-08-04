package school.faang.user_service.service.goal.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.goal.GoalInvitationFilter;
import school.faang.user_service.dto.filter.GoalInvitationFilterIDto;

import java.util.stream.Stream;

@Component
public class GoalInvitationInvitedUserNameFilter implements GoalInvitationFilter {

    @Override
    public boolean isApplicable(GoalInvitationFilterIDto filters) {
        return filters.getInvitedNamePattern() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, GoalInvitationFilterIDto filters) {
        return invitations.filter(invitation -> invitation.getInvited().getUsername().equals(filters.getInvitedNamePattern()));
    }
}
