package school.faang.user_service.utils.validationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalInvitationValidation {
    private static final String ERROR_INVITER_ID_NULL = "Inviter ID and Invited User ID cannot be null";
    private static final String ERROR_SAME_IDS = "Inviter ID and Invited User ID must be different";
    private static final String ERROR_INVITED_NOT_FOUND = "Invited User doesn't exist in DB";
    private static final String ERROR_INVITER_NOT_FOUND = "Inviter User doesn't exist in DB";
    private static final String GOAL_INVITATION_DOES_NOT_EXIST = "Goal invitation with id %d doesn't exist in DB";
    private static final String INVITED_USER_CANNOT_BE_NULL = "Invited user can't be null";
    private static final String RECEIVED_GOAL_INVITATIONS_CANNOT_BE_NULL = "Received goal invitations for the invited user cannot be null.";

    private final GoalInvitationRepository goalInvitationRepository;

    public void validateInvitedUser(GoalInvitation goalInvitation) {
        if (goalInvitation.getInvited() == null) {
            log.error(INVITED_USER_CANNOT_BE_NULL);
            throw new DataValidationException(INVITED_USER_CANNOT_BE_NULL);
        }
    }

    public void validateReceivedInvitations(GoalInvitation goalInvitation) {
        if (goalInvitation.getInvited().getReceivedGoalInvitations() == null) {
            log.error(RECEIVED_GOAL_INVITATIONS_CANNOT_BE_NULL);
            throw new DataValidationException(RECEIVED_GOAL_INVITATIONS_CANNOT_BE_NULL);
        }
    }

    public void validateGoalInvitationDtoForCreation(GoalInvitationDto invitationDto) {
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

    public void validateGoalInvitationForAcceptance(GoalInvitation goalInvitation) {
        log.info("Validating GoalInvitation with ID: {}", goalInvitation.getId());
        if (!goalInvitationRepository.existsById(goalInvitation.getId())) {
            log.error(String.format(GOAL_INVITATION_DOES_NOT_EXIST, goalInvitation.getId()));
            throw new DataValidationException(String.format(GOAL_INVITATION_DOES_NOT_EXIST, goalInvitation.getId()));
        }
        validateInvitedUser(goalInvitation);
        validateReceivedInvitations(goalInvitation);
        log.info("GoalInvitation validation passed successfully");
    }

    public void validateGoalInvitationForRejection(GoalInvitation goalInvitation) {
        log.info("Validating GoalInvitation with ID: {}", goalInvitation.getId());
        if (!goalInvitationRepository.existsById(goalInvitation.getId())) {
            log.error(String.format(GOAL_INVITATION_DOES_NOT_EXIST, goalInvitation.getId()));
            throw new DataValidationException(String.format(GOAL_INVITATION_DOES_NOT_EXIST, goalInvitation.getId()));
        }
        validateInvitedUser(goalInvitation);
        validateReceivedInvitations(goalInvitation);
        log.info("GoalInvitation validation passed successfully");
    }
}