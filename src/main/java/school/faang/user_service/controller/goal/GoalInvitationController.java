package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.util.goal.exception.CreateInvitationException;
import school.faang.user_service.util.goal.validator.GoalInvitationControllerValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal/invitation")
public class GoalInvitationController {

    private final GoalInvitationControllerValidator goalInvitationValidator;
    private final GoalInvitationService goalInvitationService;

    @PostMapping("/create")
    public ResponseEntity<?> createInvitation(@RequestBody GoalInvitationDto goalInvitationDto) {
        goalInvitationValidator.validateInvitation(goalInvitationDto, new CreateInvitationException());

        return ResponseEntity.ok(goalInvitationService.createInvitation(goalInvitationDto));
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<?> acceptGoalInvitation(@PathVariable Long id) {
        goalInvitationValidator.validateInvitation(id);

        return ResponseEntity.ok(goalInvitationService.acceptGoalInvitation(id));
    }
}
