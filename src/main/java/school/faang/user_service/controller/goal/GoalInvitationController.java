package school.faang.user_service.controller.goal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@Controller
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalInvitationController {
    GoalInvitationService goalInvitationService;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation) {
        return goalInvitationService.createInvitation(invitation);
    }

    public void acceptGoalInvitation(long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    public void rejectGoalInvitation(long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
