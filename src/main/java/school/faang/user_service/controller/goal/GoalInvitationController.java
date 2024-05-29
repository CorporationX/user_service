package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping("/createInvitation")
    public GoalInvitationDto createInvitation(@Valid @RequestBody GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("/acceptGoalInvitation/{id}")
    public void acceptGoalInvitation(@Min(1) @PathVariable long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("/rejectGoalInvitation/{id}")
    public void rejectGoalInvitation(@Min(1) @PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping("/getInvitations")
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
