package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/invitation")
public class GoalInvitationController {
    private final GoalInvitationService service;

    @PostMapping("/create")
    @ResponseBody
    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        return service.createInvitation(invitation);
    }
}