package school.faang.user_service.utils.validationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalInvitationValidation {
    private static final int MAX_ACTIVE_GOALS = 3;
    private static final String ERROR_INVITER_ID_NULL = "Inviter ID and Invited User ID cannot be null";
    private static final String ERROR_SAME_IDS = "Inviter ID and Invited User ID must be different";
    private static final String ERROR_INVITED_NOT_FOUND = "Invited User doesn't exist in DB";
    private static final String ERROR_INVITER_NOT_FOUND = "Inviter User doesn't exist in DB";
    private static final String GOAL_INVITATION_DOES_NOT_EXIST = "Goal invitation with id %d doesn't exist in DB";

    private final GoalInvitationRepository goalInvitationRepository;

    public void validateGoalInvitationDtoInCreation(GoalInvitationDto invitationDto) {
        log.info("Validating GoalInvitationDto: {}", invitationDto);
        if (invitationDto.inviterId() == null || invitationDto.invitedUserId() == null) {
            log.error(ERROR_INVITER_ID_NULL);
            throw new DataValidationException(ERROR_INVITER_ID_NULL);
        }
        if (Objects.equals(invitationDto.inviterId(), invitationDto.invitedUserId())) {
            log.error(ERROR_SAME_IDS);
            throw new DataValidationException(ERROR_SAME_IDS);
        }
        if (!goalInvitationRepository.existsByInvitedId(invitationDto.invitedUserId())) {
            log.error(ERROR_INVITED_NOT_FOUND);
            throw new DataValidationException(ERROR_INVITED_NOT_FOUND);
        }
        if (!goalInvitationRepository.existsByInviterId(invitationDto.inviterId())) {
            log.error(ERROR_INVITER_NOT_FOUND);
            throw new DataValidationException(ERROR_INVITER_NOT_FOUND);
        }
        log.info("GoalInvitationDto validation passed successfully");
    }

    public boolean validateGoalInvitationInAccept(GoalInvitation goalInvitation) {
        log.info("Validating GoalInvitation with ID: {}", goalInvitation.getId());
        User invitedUser = goalInvitation.getInvited();
        if (invitedUser.getReceivedGoalInvitations().size() >= MAX_ACTIVE_GOALS) {
            log.warn("User with ID {} has reached the active target limit", goalInvitation.getId());
            goalInvitation.setStatus(RequestStatus.REJECTED);
            return false;
        }
        if (invitedUser.getReceivedGoalInvitations().contains(goalInvitation)) {
            log.warn("User with ID {} is already working on this goal", goalInvitation.getId());
            return false;
        }
        if (!goalInvitationRepository.existsById(goalInvitation.getId())) {
            goalInvitation.setStatus(RequestStatus.REJECTED);
            log.error(String.format(GOAL_INVITATION_DOES_NOT_EXIST, goalInvitation.getId()));
            throw new DataValidationException(String.format(GOAL_INVITATION_DOES_NOT_EXIST, goalInvitation.getId()));
        }
        log.info("GoalInvitation validation passed successfully");
        return true;
    }
}