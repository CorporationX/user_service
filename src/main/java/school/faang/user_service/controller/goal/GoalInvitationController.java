package school.faang.user_service.controller.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.GoalInvitationService;

@Controller
@Data
@AllArgsConstructor
public class GoalInvitationController {
    private GoalInvitationService goalInvitationService;

    public void createInvitation(GoalInvitationDto invitation) {
        goalInvitationService.createInvitation(invitation);
    }
}
