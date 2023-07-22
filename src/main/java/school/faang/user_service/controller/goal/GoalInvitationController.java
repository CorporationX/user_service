package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.validator.GoalInvitationValidator;

@Controller
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    private final GoalInvitationValidator goalInvitationValidator;

    public void createInvitation(GoalInvitationDto invitation) {
        goalInvitationValidator.validateControllerInputData(invitation);
        goalInvitationService.createInvitation(invitation);
    }
}
