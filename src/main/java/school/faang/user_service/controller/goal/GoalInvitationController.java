package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/goal/invitation")
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationServiceImpl goalInvitationServiceImpl;

    @PostMapping
    public GoalInvitationDto createInvitation(@Valid GoalInvitationDto invitation) {
        return goalInvitationServiceImpl.createInvitation(invitation);
    }

    @PatchMapping("/accept")
    public GoalInvitationDto acceptGoalInvitation(long id) {
        return goalInvitationServiceImpl.acceptGoalInvitation(id);
    }
    @PatchMapping("/reject")
    public GoalInvitationDto rejectGoalInvitation(long id) {
        return goalInvitationServiceImpl.rejectGoalInvitation(id);
    }
    @GetMapping
    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        return goalInvitationServiceImpl.getInvitations(filter);
    }

}
