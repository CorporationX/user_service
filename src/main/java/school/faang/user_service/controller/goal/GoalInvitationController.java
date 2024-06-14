package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/goal-invitations")
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public GoalInvitationDto createInvitation(@Valid @RequestBody GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public void acceptGoalInvitation(@Min(1) @PathVariable long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public void rejectGoalInvitation(@Min(1) @PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
