package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.filters.filtersForGoalInvitation.GoalInvitationFilter;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        return createInvitation(invitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        return acceptGoalInvitation(id);
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        return rejectGoalInvitation(id);
    }

    public List<GoalInvitationDto> getInvitations(GoalInvitationFilter goalInvitationFilter) {
        return getInvitations(goalInvitationFilter);
    }

}
