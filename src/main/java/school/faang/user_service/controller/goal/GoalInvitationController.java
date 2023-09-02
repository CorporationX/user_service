package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.validator.GoalInvitationValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    private final GoalInvitationValidator goalInvitationValidator;

    @PostMapping("/invitation")
    public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto invitation) {
        goalInvitationValidator.validateControllerInputData(invitation);
        return goalInvitationService.createInvitation(invitation);
    }

    @PostMapping("/invitation/{id}/accept")
    public GoalInvitationDto acceptGoalInvitation(@PathVariable long id) {
        goalInvitationValidator.validateId(id);
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PostMapping("/invitation/{id}/reject")
    public GoalInvitationDto rejectGoalInvitation(@PathVariable long id) {
        goalInvitationValidator.validateId(id);
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping("/invitation/list")
    public List<GoalInvitationDto> getInvitations(@RequestBody GoalInvitationFilterDto filter) {
        goalInvitationValidator.validateFilter(filter);
        return goalInvitationService.getInvitations(filter);
    }
}
