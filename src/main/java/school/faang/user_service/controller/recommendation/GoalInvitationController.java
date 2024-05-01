package school.faang.user_service.controller.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@Controller
@Data
@AllArgsConstructor
public class GoalInvitationController {
    private GoalInvitationService goalInvitationService;

    List<GoalInvitation> getInvitations(InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
