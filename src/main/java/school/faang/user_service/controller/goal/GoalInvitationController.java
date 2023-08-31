package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}