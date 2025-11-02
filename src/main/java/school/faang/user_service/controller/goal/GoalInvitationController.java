package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.goal.CreateGoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalInvitationService;

@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    GoalInvitationDto create(long goalId, CreateGoalInvitationDto invitationDto) {
        if (invitationDto.getInvitedUserId() == null) {
            throw new DataValidationException("Invited user id should be present");
        }
        if (invitationDto.getGoalId() == null) {
            throw new DataValidationException("Goal id should be present");
        }
        return goalInvitationService.create(invitationDto.getGoalId(), invitationDto);
    }

    public void accept(long invitationId) {
        goalInvitationService.accept(invitationId);
    }

    public void reject(long invitationId) {
        goalInvitationService.reject(invitationId);
    }

}
