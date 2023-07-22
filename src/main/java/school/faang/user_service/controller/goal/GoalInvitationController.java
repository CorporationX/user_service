package school.faang.user_service.controller.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.GoalInvitationService;

@Controller
public class GoalInvitationController {
    private GoalInvitationService goalInvitationService;

    @Autowired
    private GoalInvitationController(GoalInvitationService goalInvitationService) {
        this.goalInvitationService = goalInvitationService;
    }

    public void createInvitation(GoalInvitationDto invitation) {
        goalInvitationService.createInvitation(invitation);
    }
}
