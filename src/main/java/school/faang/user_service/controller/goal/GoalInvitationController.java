package school.faang.user_service.controller.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@Controller
@Data
@AllArgsConstructor
public class GoalInvitationController {
    private GoalInvitationService goalInvitationService;

    GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    void acceptGoalInvitation(long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    void rejectGoalInvitation(long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    List<GoalInvitation> getInvitations(InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
