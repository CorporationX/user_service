package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/goal/invitation")
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping
    public ResponseEntity<GoalInvitationDto> createInvitation(GoalInvitationDto invitation) {
        return ResponseEntity.ok(goalInvitationService.createInvitation(invitation));
    }

    @PatchMapping("/accept")
    public ResponseEntity<GoalInvitationDto> acceptGoalInvitation(long id) {
        return ResponseEntity.ok(goalInvitationService.acceptGoalInvitation(id));
    }
    @PatchMapping("/reject")
    public ResponseEntity<GoalInvitationDto> rejectGoalInvitation(long id) {
        return ResponseEntity.ok(goalInvitationService.rejectGoalInvitation(id));
    }
    @GetMapping("/filter")
    public ResponseEntity<List<GoalInvitationDto>> getInvitations(InvitationFilterDto filter){
        return ResponseEntity.ok(goalInvitationService.getInvitations(filter));
    }

}
