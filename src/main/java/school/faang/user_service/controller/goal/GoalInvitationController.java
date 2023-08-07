package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.dto.filter.GoalInvitationFilterIDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping("/goal/invitation")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalInvitationDto createInvitation(@Valid @RequestBody GoalInvitationDto invitationDto) {
        return goalInvitationService.createInvitation(invitationDto);
    }

    @PutMapping("/goal/invitation/{id}/accept")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public GoalInvitationDto acceptGoalInvitation(@PathVariable Long id) {
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("/goal/invitation/{id}/reject")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public GoalInvitationDto rejectGoalInvitation(@PathVariable Long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping("/goal/invitations")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalInvitationDto> getInvitations(@Valid @RequestBody GoalInvitationFilterIDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
