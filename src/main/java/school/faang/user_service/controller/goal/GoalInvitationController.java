package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@Controller
@RequestMapping("/goals/invitations")
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping("/create")
    public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto invitationDto) {
        return goalInvitationService.createInvitation(invitationDto);
    }

    @PatchMapping("/accept/{id}")
    public GoalInvitationDto acceptGoalInvitation(@PathVariable Long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PatchMapping("/reject/{id}")
    public GoalInvitationDto rejectGoalInvitation(@PathVariable Long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping("/{id}")
    public List<GoalInvitationDto> getInvitations(@RequestParam InvitationFilterDto filters) {
        return goalInvitationService.getInvitations(filters);
    }
}
