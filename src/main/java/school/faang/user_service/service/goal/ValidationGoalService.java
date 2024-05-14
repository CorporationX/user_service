package school.faang.user_service.service.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.ExceptionMessages;
import school.faang.user_service.exceptions.UpdatingCompletedGoalException;
import school.faang.user_service.exceptions.UserGoalsValidationException;

@Component
class ValidationGoalService {
    private final static int MAX_ACTIVE_GOALS = 3;

    public void checkMaxActiveGoals(int count) {
        if (count >= MAX_ACTIVE_GOALS) {
            throw new UserGoalsValidationException(ExceptionMessages.MAX_ACTIVE_GOALS_IS_OVER.getMessage());
        }
    }

    public void checkGoalStatusForCompleted(boolean flag) {
        if (flag) {
            throw new UpdatingCompletedGoalException(ExceptionMessages.GOAL_STATUS_IS_COMPLETED.getMessage());
        }
    }

    public void checkSizeSkillIdsDontEqualsExistingSkillsInDB(boolean flag) {
        if (!flag) {
            throw new DataValidationException(ExceptionMessages.NON_EXIST_SKILLS.getMessage());
        }
    }

}
