package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/goal")
public class GoalInvitationController {
    private final GoalInvitationService invitationService;

    @PostMapping
    public GoalInvitationDto createInvitation(@Valid @RequestBody GoalInvitationDto invitationDto) {
        return invitationService.creatInvitation(invitationDto);
    }

    @PutMapping("/{id}/accept")
    public GoalInvitationDto acceptGoalInvitation(@PathVariable long id) {
        return invitationService.acceptGoalInvitation(id);
    }

    @PutMapping("/{id}/reject")
    public GoalInvitationDto rejectGoalInvitation(@PathVariable long id) {
        return invitationService.rejectGoalInvitation(id);
    }

    @GetMapping
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filterDto) {
        return invitationService.getInvitations(filterDto);
    }
}
