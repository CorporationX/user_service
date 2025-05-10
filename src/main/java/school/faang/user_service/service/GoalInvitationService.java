package school.faang.user_service.service;

import school.faang.user_service.dto.goal.GoalInvitationDto;

public interface GoalInvitationService {
    GoalInvitationDto createInvitation(GoalInvitationDto invitation);

    void acceptGoalInvitation(long id);
}
