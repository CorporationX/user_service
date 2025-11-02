package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.CreateGoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;

import java.util.List;

public interface GoalInvitationService {

    GoalInvitationDto create(long goalId, CreateGoalInvitationDto invitationDto);

    void accept(long invitationId);

    void reject(long invitationId);

    List<GoalInvitationDto> getByFilters(GoalInvitationFilterDto filters);

}
