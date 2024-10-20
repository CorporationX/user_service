package school.faang.user_service.integration.factory;

import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.integration.CommonFactory;

import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;

public class GoalFactory extends CommonFactory {
    public static CreateGoalDto buildDefaultCreateGoalDto(Long userId) {
        return CreateGoalDto.builder()
                .userId(userId)
                .title(GOAL_TITLE)
                .description(GOAL_DESCRIPTION)
                .build();
    }

    public static UpdateGoalDto buildDefaultUpdateGoalDto(Long goalId) {
        return UpdateGoalDto.builder()
                .goalId(goalId)
                .title(GOAL_TITLE_UPDATED)
                .description(GOAL_DESCRIPTION_UPDATED)
                .status(ACTIVE)
                .build();
    }
}
