package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;

public abstract class CommonGoalTest {
    protected static final Long USER_ID = 1L;
    protected static final Long GOAL_ID = 2L;
    protected static final Long PARENT_GOAL_ID = 3L;
    protected static final Long SKILL_ID = 4L;
    protected static final String GOAL_TITLE = "Java Developer";
    protected static final String GOAL_DESCRIPTION = "Become a Java developer";
    protected static final List<Long> SKILL_IDS = List.of(SKILL_ID);
    protected static final String GOAL_FILTER_TITLE = "Developer";
    protected static final String GOAL_FILTER_DESCRIPTION = "Java";

    protected Goal createGoal() {
        return Goal.builder()
            .id(GOAL_ID)
            .title(GOAL_TITLE)
            .description(GOAL_DESCRIPTION)
            .status(ACTIVE)
            .build();
    }

    protected User createUser() {
        return User.builder()
            .id(USER_ID)
            .build();
    }

    protected GoalFilterDto createGoalFilterDto() {
        return GoalFilterDto.builder()
            .title(GOAL_FILTER_TITLE)
            .description(GOAL_FILTER_DESCRIPTION)
            .status(ACTIVE)
            .build();
    }
}
