package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.GoalInvitationService;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping
    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        return goalInvitationService.createInvitation(invitationDto);
    }

    @PostMapping("/rejectInvitation/{id}")
    public void rejectGoalInvitation(long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }
}