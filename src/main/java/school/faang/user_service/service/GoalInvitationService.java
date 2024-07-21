package school.faang.user_service.service;

import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import java.util.List;

public interface GoalInvitationService {
    GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto);

    GoalInvitationDto acceptGoalInvitation(long id);

    GoalInvitationDto rejectGoalInvitation(long id);

    List<GoalInvitationDto> getInvitations(InvitationFilterDto filters);
}
