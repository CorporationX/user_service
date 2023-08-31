package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invitations")
public class GoalInvitationController {

    private final GoalInvitationService service;

    @PostMapping()
    public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto invitation) {
        return service.createInvitation(invitation);
    }
    @GetMapping
    public GoalInvitationDto acceptGoalInvitation(long id) {
       return service.acceptGoalInvitation(id);
    }

}