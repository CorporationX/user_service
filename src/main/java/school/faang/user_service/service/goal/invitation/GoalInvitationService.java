package school.faang.user_service.service.goal.invitation;

import school.faang.user_service.dto.goal.GoalInvitationCreateDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

public interface GoalInvitationService {

    GoalInvitation findById(long id);

    void createInvitation(GoalInvitationCreateDto invitation);

    void acceptGoalInvitation(long id);

    void rejectGoalInvitation(long id);

    List<GoalInvitationDto> getInvitations(InvitationFilterDto filterDto);
}
