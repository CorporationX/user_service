package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import java.util.List;

public interface GoalInvitationService {
    GoalInvitationDto createInvitation(GoalInvitationDto invitation);

    void acceptGoalInvitation(long id);

    void rejectGoalInvitation(long id);

    List<GoalInvitationDto> getAllInvitations(InvitationFilterDto invitationFilterDto);
}
