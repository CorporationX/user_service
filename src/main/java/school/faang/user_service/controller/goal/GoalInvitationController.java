package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.GoalInvitationService;

@RestController
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation){
        return goalInvitationService.createInvitation(invitation);

    }
}
