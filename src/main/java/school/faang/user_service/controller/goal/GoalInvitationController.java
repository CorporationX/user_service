package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;

@Controller
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService service;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        return service.createInvitation(invitation);
    }
}