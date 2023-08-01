package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal/invitation")
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    @PostMapping()
    public GoalInvitationDto createInvitation(@RequestBody GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    @PutMapping("accept/{id}")
    public GoalInvitationDto acceptGoalInvitation(@RequestBody long id) {
        validateId(id);
        return goalInvitationService.acceptGoalInvitation(id);
    }

    @PutMapping("reject/{id}")
    public GoalInvitationDto rejectGoalInvitation(@RequestBody long id) {
        validateId(id);
        return goalInvitationService.rejectGoalInvitation(id);
    }

    @PostMapping("/filters")
    public List<GoalInvitationDto> getInvitations(@RequestBody InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }

    private void validateId(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Invalid request. Id can't be less than 0.");
        }
    }
}
