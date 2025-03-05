package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    public void createInvitation(GoalInvitationDto invitationDto) {
        log.info("Creating goal invitation for: {}", invitationDto);
        goalInvitationService.createInvitation(invitationDto);
    }

    public void acceptGoalInvitation(long id) {
        log.info("Accepting goal invitation with ID: {}", id);
        goalInvitationService.acceptGoalInvitation(id);
    }

    public void rejectGoalInvitation(long id) {
        log.info("Rejecting goal invitation with ID: {}", id);
        goalInvitationService.rejectGoalInvitation(id);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filterDto) {
        return goalInvitationService.getInvitations(filterDto);
    }
}
