package school.faang.user_service.testConstants;

import java.util.List;

public interface GoalTestConstants {

    Long USER_ID = 1L;
    Long VALID_GOAL_ID = 1L;
    Long PARENT_GOAL_ID = 1L;
    Long INVALID_GOAL_ID = -1L;
    Long INVALID_USER_ID = -1L;
    String FILTER_FIELD = "id";
    String GOAL_TITLE = "Learn Java";
    String GOAL_DESCRIPTION = "Master Java Core and Spring Boot";
    String ACTIVE_GOAL_EXCEPTION_MESSAGE = "User with ID %d cannot have more than %d active goals.";
    String GOAL_DOES_NOT_EXIST_EXCEPTION_MESSAGE = "Goal with ID %d does not exist.";
    String VALID_SKILLS_EXCEPTION_MESSAGE = "Some of the provided skills do not exist in the database.";
    String ASSERT_EXCEPTION_MESSAGE = "Expected IllegalArgumentException occurred";
    int MAX_ACTIVE_GOALS = 3;
    int EXCEEDING_ACTIVE_GOALS = 5;
    int WITHIN_ACTIVE_GOALS = 2;
    int EXISTING_SKILLS_COUNT = 0;
    int ACTIVE_GOALS_COUNT = 0;
    List<Long> SKILL_IDS = List.of(1L, 2L);
    List<Long> USER_IDS = List.of(1L, 2L);

}
