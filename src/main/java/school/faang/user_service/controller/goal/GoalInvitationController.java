package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping
    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    public GoalInvitationDto acceptGoalInvitation(long id) {
        return goalInvitationService.acceptInvitation(id);
    }

    public GoalInvitationDto rejectGoalInvitation(long id) {
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}