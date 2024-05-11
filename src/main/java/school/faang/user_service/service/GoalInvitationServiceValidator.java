package school.faang.user_service.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static school.faang.user_service.exception.MessageForGoalInvitationService.*;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalInvitationServiceValidator {

    GoalInvitationRepository goalInvitationRepository;
    GoalRepository goalRepository;
    UserRepository userRepository;

    void validateForCreateInvitation(GoalInvitationDto goalInvitationDto) {
        if (goalInvitationDto == null) {
            throw new DataValidationException(INPUT_IS_NULL.getMessage());
        }
        if (goalInvitationDto.getInviterId() == null) {
            throw new DataValidationException(INVITER_ID_IS_NULL.getMessage());
        }
        if (goalInvitationDto.getInvitedUserId() == null) {
            throw new DataValidationException(INVITED_USER_ID_IS_NULL.getMessage());
        }
        if (goalInvitationDto.getInviterId().equals(goalInvitationDto.getInvitedUserId())) {
            throw new DataValidationException(INVITER_ID_EQUALS_INVITED_USER_ID.getMessage());
        }
        if (userRepository.findById(goalInvitationDto.getInviterId()).isEmpty()) {
            throw new DataValidationException(NO_INVITER_IN_DB.getMessage());
        }
        if (userRepository.findById(goalInvitationDto.getInvitedUserId()).isEmpty()) {
            throw new DataValidationException(NO_INVITED_IN_DB.getMessage());
        }
        if (goalRepository.findById(goalInvitationDto.getGoalId()).isEmpty()) {
            throw new DataValidationException(NO_GOAL_IN_DB.getMessage());
        }
    }

    void validateForAcceptGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).orElseThrow(() ->
                new DataValidationException(NO_GOAL_INVITATION_IN_DB.getMessage()));
        Goal goal = goalRepository.findById(goalInvitation.getGoal().getId()).orElseThrow(() ->
                new DataValidationException(NO_GOAL_IN_DB.getMessage()));

        User invited = goalInvitation.getInvited();

        if (invited == null) {
            throw new DataValidationException(NO_INVITED_IN_GOAL_INVITATION.getMessage());
        }
        if (invited.getSetGoals() == null) {
            throw new DataValidationException(SET_GOALS_IS_NULL.getMessage());
        }

        List<Goal> setGoals = goalInvitation.getInvited().getSetGoals();

        if (setGoals.size() > GoalInvitationService.SETGOAL_SIZE) {
            throw new DataValidationException(MORE_THEN_THREE_GOALS.getMessage());
        }
        if (setGoals.contains(goal)) {
            throw new DataValidationException(INVITED_HAS_GOAL.getMessage());
        }
    }

    void validateForRejectGoalInvitation(long id) {
        GoalInvitation goalInvitation = goalInvitationRepository.findById(id).
                orElseThrow(() -> new DataValidationException(NO_GOAL_INVITATION_IN_DB.getMessage()));
        if (goalRepository.findById(goalInvitation.getGoal().getId()).isEmpty()) {
            throw new DataValidationException(NO_GOAL_IN_DB.getMessage());
        }
    }

    void validateForGetInvitations(InvitationFilterDto filters) {
        if (filters == null) {
            throw new DataValidationException(INPUT_IS_NULL.getMessage());
        }
    }
}
