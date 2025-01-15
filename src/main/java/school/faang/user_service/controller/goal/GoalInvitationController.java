package school.faang.user_service.controller.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.GoalInvitationService;

import java.util.List;

@Component
public class GoalInvitationController {


    private final GoalInvitationService goalInvitationService;

    @Autowired
    public GoalInvitationController(GoalInvitationService goalInvitationService) {
        this.goalInvitationService = goalInvitationService;
    }

    public void createInvitation(GoalInvitationDto dto){
        goalInvitationService.createInvitation(dto);
    }

    public void acceptGoalInvitation(long id){
        goalInvitationService.acceptGoalInvitation(id);
    }

    public void rejectGoalInvitation(Long id){
        goalInvitationService.rejectGoalInvitation(id);
    }

    public List<GoalInvitationDto> getInvitations(InvitationFilterDto filters){
        return goalInvitationService.getInvitations(filters);
    }

}
