package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goals/invitations")
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping("/creation")
    public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("/acceptance")
    public GoalInvitationDto acceptGoalInvitation(@RequestParam long id) {
        return goalInvitationService.acceptInvitation(id);
    }

    @PutMapping("/rejection")
    public GoalInvitationDto rejectGoalInvitation(@RequestParam long id) {
        return goalInvitationService.rejectInvitation(id);
    }

    @PostMapping("/invitations-by-filter")
    public List<GoalInvitationDto> getInvitationsByFilter(@RequestBody InvitationFilterDto invitationFilterDto) {
        return goalInvitationService.getInvitations(invitationFilterDto);
    }
}
