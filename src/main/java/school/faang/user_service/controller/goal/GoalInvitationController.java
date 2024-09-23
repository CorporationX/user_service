package school.faang.user_service.controller.goal;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/invitation")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping("/create")
    public ResponseEntity<GoalInvitationDto> createInvitation (@RequestBody GoalInvitationDto invitation){
        GoalInvitationDto invitationDto = goalInvitationService.createInvitation(invitation);
        return ResponseEntity.ok(invitationDto);
    }
    @PutMapping("/accept/id")
    public ResponseEntity<String> acceptGoalInvitation(@PathVariable long id){
        goalInvitationService.acceptGoalInvitation(id);
        return ResponseEntity.ok("Приглашение принято.");
    }

    @DeleteMapping("/reject/id")
    public ResponseEntity<String> rejectGoalInvitation(@PathVariable long id){
        goalInvitationService.rejectGoalInvitation(id);
        return ResponseEntity.ok("Приглашение отклонено.");
    }

    public List<GoalInvitation> getInvitations(@RequestBody InvitationFilterDto filter){
       return goalInvitationService.getInvitations(filter);
    }
}

