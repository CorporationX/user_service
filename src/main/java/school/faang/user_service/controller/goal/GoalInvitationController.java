package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    public void createInvitation(GoalInvitationDto invitation) {
        goalInvitationService.createInvitation(invitation);
    }

    public void acceptGoalInvitation(long id) {
        validateId(id);
        goalInvitationService.acceptGoalInvitation(id);
    }

    public void rejectGoalInvitation(long id) {
        validateId(id);
        goalInvitationService.rejectGoalInvitation(id);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }

    private void validateId(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Invalid request. Id can't be less than 0.");
        }
    }
}
