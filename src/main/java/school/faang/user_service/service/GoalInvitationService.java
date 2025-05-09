package school.faang.user_service.service;

import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;

public interface GoalInvitationService {
    GoalInvitation createInvitation(GoalInvitationDto invitation);
}
