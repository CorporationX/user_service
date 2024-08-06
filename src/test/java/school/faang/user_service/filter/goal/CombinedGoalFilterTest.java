package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CombinedGoalFilterTest {

    GoalDescriptionFilter goalDescriptionFilter = new GoalDescriptionFilter();
    GoalStatusFilter goalStatusFilter = new GoalStatusFilter();
    GoalParentIdFilter goalParentIdFilter = new GoalParentIdFilter();
    GoalFilterDto goalFilterDto = new GoalFilterDto();
    List<Goal> goals;

    @BeforeEach
    public void setUp() {
        Goal parentGoal = Goal.builder()
                .id(1L)
                .build();
        Goal goalFirst = Goal.builder()
                .title("first")
                .description("description")
                .status(GoalStatus.ACTIVE)
                .parent(parentGoal)
                .build();
        Goal goalSecond = Goal.builder()
                .title("second")
                .description("description")
                .status(GoalStatus.COMPLETED)
                .build();
        goals = List.of(goalFirst, goalSecond);
    }

    @Test
    void testCombinedFiltersWhenAllFiltersExist() {
        goalFilterDto.setDescriptionPattern("des");
        goalFilterDto.setStatusPattern(GoalStatus.COMPLETED);
        goalFilterDto.setParentIdPattern(1L);
        List<Goal> descriptionFilterResult = goalDescriptionFilter.apply(goals.stream(), goalFilterDto).toList();
        assertEquals(goals.size(), descriptionFilterResult.size());
        List<Goal> statusFilterResult = goalStatusFilter.apply(descriptionFilterResult.stream(), goalFilterDto).toList();
        assertEquals(1, statusFilterResult.size());
        List<Goal> result = goalParentIdFilter.apply(statusFilterResult.stream(), goalFilterDto).toList();
        assertTrue(result.isEmpty());
    }
}
