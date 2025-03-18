package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.GoalInvitationService;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/goal-invitations")
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping
    public void createInvitation(@Valid @RequestBody GoalInvitationDto invitationDto) {
        log.info("Received request to create invitation: {}", invitationDto);
        goalInvitationService.createInvitation(invitationDto);
    }

    @PutMapping("/{id}/accept")
    public void acceptGoalInvitation(@PathVariable Long id) {
        log.info("Received request to accept invitation with ID: {}", id);
        goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("/{id}/reject")
    public void rejectGoalInvitation(@PathVariable Long id) {
        log.info("Received request to reject invitation with ID: {}", id);
        goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping
    public List<GoalInvitationDto> getInvitations(
            @RequestParam(required = false) @PositiveOrZero Long inviterId,
            @RequestParam(required = false) @PositiveOrZero Long invitedId,
            @RequestParam(required = false) RequestStatus status) {
        log.info("Received request to fetch invitations with inviterId: {}, invitedId: {}, status: {}",
                inviterId, invitedId, status);

        InvitationFilterDto filter = new InvitationFilterDto(inviterId, invitedId, status);
        return goalInvitationService.getInvitations(filter);
    }

    @GetMapping("/invited/{invitedUserId}")
    public List<GoalInvitationDto> getInvitationsByInvitedUserId(@PathVariable Long invitedUserId) {
        log.info("Received request to fetch invitations for invited user ID: {}", invitedUserId);
        return goalInvitationService.getInvitationsByInvitedUserId(invitedUserId);
    }
}