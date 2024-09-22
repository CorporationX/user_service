package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goals_invitations")
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("/{id}/accepting")
    public GoalInvitationDto acceptGoalInvitation(@PathVariable long id) {
        return goalInvitationService.acceptInvitation(id);
    }

    @PutMapping("/{id}/rejection")
    public GoalInvitationDto rejectGoalInvitation(@PathVariable long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}