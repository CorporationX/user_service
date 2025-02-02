package school.faang.user_service.controller.invitation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.invitation.GoalInvitationDto;
import school.faang.user_service.dto.invitation.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.invitation.GoalInvitationService;

@RestController
@RequestMapping("/api/v1/goals/invitation")
@RequiredArgsConstructor
public class GoalInvitationController {
  private final GoalInvitationService goalInvitationService;

  @PostMapping
  public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto invitation) {
    return goalInvitationService.createInvitation(invitation);
  }

  @PostMapping("/accept/{id}")
  public void acceptGoalInvitation(@PathVariable Long id) {
    goalInvitationService.acceptGoalInvitation(id);
  }

  @PostMapping("/reject/{id}")
  public void rejectGoalInvitation(@PathVariable Long id) {
    goalInvitationService.rejectGoalInvitation(id);
  }

  @GetMapping
  public List<GoalInvitationDto> getAll(
      @RequestParam(required = false) Long inviterId,
      @RequestParam(required = false) Long invitedId,
      @RequestParam(required = false) String inviterNamePattern,
      @RequestParam(required = false) String invitedNamePattern,
      @RequestParam(required = false) RequestStatus status) {
    InvitationFilterDto filterDto =
        new InvitationFilterDto(
            inviterNamePattern, invitedNamePattern, inviterId, invitedId, status);
    return goalInvitationService.getInvitations(filterDto);
  }
}
