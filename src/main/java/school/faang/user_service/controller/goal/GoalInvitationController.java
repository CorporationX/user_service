package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("api/v1/goal-invitations")
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping
    public GoalInvitationDto createInvitation(@Valid @RequestBody GoalInvitationDto invitationDto) {
        return goalInvitationService.createInvitation(invitationDto);
    }

    @PutMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    public void acceptGoalInvitation(@PathVariable Long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public void rejectGoalInvitation(@PathVariable Long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping
    public List<GoalInvitationDto> getInvitations(GoalInvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
