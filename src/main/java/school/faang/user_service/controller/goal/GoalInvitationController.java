package school.faang.user_service.controller.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.GoalInvitationService;

@Component
public class GoalInvitationController {
    private GoalInvitationService goalInvitationService;

    public GoalInvitationController(GoalInvitationService goalInvitationService) {
        this.goalInvitationService = goalInvitationService;
    }
    void createInvitation(GoalInvitationDto invitation) {

    }
}
