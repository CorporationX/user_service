package school.faang.user_service.service;

import school.faang.user_service.model.dto.GoalInvitationDto;
import school.faang.user_service.model.filter_dto.InvitationFilterDto;

import java.util.List;

public interface GoalInvitationService {
    GoalInvitationDto createInvitation(GoalInvitationDto goalInvitationDto);

    void acceptGoalInvitation(long id);

    void rejectGoalInvitation(long id);

    List<GoalInvitationDto> getInvitations(InvitationFilterDto filterDto);

    void setMaxActiveUserGoals(int maxActiveUserGoals);
}
