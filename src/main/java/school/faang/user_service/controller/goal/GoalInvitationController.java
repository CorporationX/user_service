package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.service.GoalInvitationService;

@Controller
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    private final GoalInvitationMapper goalInvitationMapper;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitation){
        GoalInvitation createdInvitation = goalInvitationService.createInvitation(invitation);
        return goalInvitationMapper.gInvitationToGIDTO(createdInvitation);
    }
}
