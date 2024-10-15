package school.faang.user_service.controller.goal;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.GoalInvitationDto;
import school.faang.user_service.dto.SuccessResponse;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.GoalInvitationService;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/invitation")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping("/create")
    public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto invitationDTO) {
        return goalInvitationService.createInvitation(invitationDTO);
    }

    @PutMapping("/accept/id")
    public ResponseEntity<SuccessResponse> acceptGoalInvitation(@PathVariable long id) {
        goalInvitationService.acceptGoalInvitation(id);
        return ResponseEntity.ok(new SuccessResponse("Приглашение принято."));
    }

    @DeleteMapping("/reject/id")
    public ResponseEntity<SuccessResponse> rejectGoalInvitation(@PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
        return ResponseEntity.ok(new SuccessResponse("Приглашение отклонено."));
    }

    @PostMapping("/getInvitations")
    public List<GoalInvitation> getInvitations(@RequestBody GoalInvitationFilterDto filterDTO) {
        return goalInvitationService.getInvitationsByFilter(filterDTO);
    }
}

