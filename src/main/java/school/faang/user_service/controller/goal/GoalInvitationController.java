package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.validator.GoalInvitationValidator;

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
    public GoalInvitationDto acceptGoalInvitation(long id) {
        goalInvitationValidator.validateId(id);
        return goalInvitationService.acceptGoalInvitation(id);
    }
}
