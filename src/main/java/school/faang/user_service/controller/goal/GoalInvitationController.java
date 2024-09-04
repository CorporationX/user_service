package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/goal-invitations")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @GetMapping
    public ResponseEntity<List<GoalInvitationDto>> getInvitations(InvitationFilterDto filter) {
        return ResponseEntity.ok(
                goalInvitationService.getInvitations(filter)
        );
    }

    @PostMapping
    public ResponseEntity<GoalInvitationDto> createInvitation(@RequestBody GoalInvitationDto invitation) {
        return ResponseEntity.ok(
                goalInvitationService.createInvitation(invitation)
        );
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<GoalInvitationDto> acceptGoalInvitation(@PathVariable long id) {
        return ResponseEntity.ok(
                goalInvitationService.acceptGoalInvitation(id)
        );
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<GoalInvitationDto> rejectGoalInvitation(@PathVariable long id) {
        return ResponseEntity.ok(
                goalInvitationService.rejectGoalInvitation(id)
        );
    }
}
