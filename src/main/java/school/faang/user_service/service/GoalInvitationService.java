package school.faang.user_service.service;

import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import java.util.List;

public interface GoalInvitationService {
    GoalInvitationDto createInvitation(Long inviterId, GoalInvitationDto invitation);

    GoalInvitationDto acceptGoalInvitation(Long goalInvitationId, Long invitedId);

    GoalInvitationDto rejectGoalInvitation(Long goalInvitationId, Long invitedId);

    List<GoalInvitationDto> getInvitations(InvitationFilterDto filter, Integer from, Integer size);
}