package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalInvitationValidator {

    private static final int MAX_GOALS_PER_USER = 3;
    private final GoalService goalService;

    public void validateDto(GoalInvitationDto invitation) {
        if (invitation.getInviterId().equals(invitation.getInvitedUserId())) {
            throw new DataValidationException("You can't invited yourself");
        }
    }

    public void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new DataValidationException("Id must be a positive number");
        }
    }

    public void validateGoalInvitationAcceptance(GoalInvitation goalInvitation) {
        User invited = goalInvitation.getInvited();
        Goal goal = goalInvitation.getGoal();
        List<Goal> goalsByInvited = invited.getGoals();

        if (goalsByInvited.size() >= MAX_GOALS_PER_USER) {
            throw new DataValidationException("Max goals need be less 3");
        }

        if (goalsByInvited.contains(goal)) {
            throw new DataValidationException("User already have this goal");
        }
    }

    public void validateGoalInvitationRejection(GoalInvitation goalInvitation) {
        Goal goal = goalInvitation.getGoal();
        goalService.findGoalById(goal.getId());
    }
}
