package school.faang.user_service.controller.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.ExceptionMessages;

@Component
class ValidationGoal {

    public void checkGoalTitleForBlank(GoalDto goalDto) {
        if (goalDto.getTitle().isBlank()) {
            throw new DataValidationException(ExceptionMessages.GOAL_TITLE_IS_BLANK.getMessage());
        }
    }
}
