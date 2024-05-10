package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationCreateDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.invitation.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("goals/invitations")
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping
    public void createInvitation(GoalInvitationCreateDto invitation) {
        goalInvitationService.createInvitation(invitation);
    }

    @PatchMapping("accept")
    public void acceptGoalInvitation(@RequestParam long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    @PatchMapping("reject")
    public void rejectGoalInvitation(@RequestParam long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    @PostMapping("get")
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
