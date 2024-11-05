package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal-invitations")
@Validated
@Slf4j
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping()
    public GoalInvitationDto createInvitation(@Validated(GoalInvitationDto.Before.class) @RequestBody GoalInvitationDto goalInvitationDto) {
        log.info("Received request to create a goal invitation for the goal with ID: {}", goalInvitationDto.getGoalId());
        return goalInvitationService.createInvitation(goalInvitationDto);
    }

    @PatchMapping("/{id}/accept")
    public GoalInvitationDto acceptGoalInvitation(@PathVariable("id") long id) {
        log.info("Received request to accept the goal invitation with ID: {}", id);
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PatchMapping("/{id}/reject")
    public GoalInvitationDto rejectGoalInvitation(@PathVariable("id") long id) {
        log.info("Received request to reject the goal invitation with ID: {}", id);
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping()
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterDto filters) {
        return goalInvitationService.getInvitations(filters);
    }
}
