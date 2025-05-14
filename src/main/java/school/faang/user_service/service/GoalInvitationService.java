package school.faang.user_service.service;

import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterIDto;

import java.util.List;

public interface GoalInvitationService {
    GoalInvitationDto createInvitation(GoalInvitationDto invitation);

    void acceptGoalInvitation(long id);

    void rejectGoalInvitation(long id);

    List<GoalInvitationDto> getInvitations(InvitationFilterIDto filter);
}
