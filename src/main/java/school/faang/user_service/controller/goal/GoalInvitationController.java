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

    public Long createInvitation(GoalInvitationDto invitationDto) {
        log.info("Creating goal invitation for: {}", invitationDto);
        Long id = goalInvitationService.createInvitation(invitationDto).getId();
        log.info("Goal invitation for: {} created successfully", invitationDto);
        return id;
    }

    public void acceptGoalInvitation(long id) {
        log.info("Accepting goal invitation with ID: {}", id);
        goalInvitationService.acceptGoalInvitation(id);
        log.info("Goal invitation with ID: {} accepted successfully", id);

    }

    public void rejectGoalInvitation(long id) {
        log.info("Rejecting goal invitation with ID: {}", id);
        goalInvitationService.rejectGoalInvitation(id);
        log.info("Goal invitation with ID: {} rejected successfully", id);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filterDto) {
        log.info("Getting goal invitations with filter {}", filterDto);
        var goalInvitations = goalInvitationService.getInvitations(filterDto);
        log.info("Goal invitations with filter {} received successfully", filterDto);
        return goalInvitations;
    }
}
