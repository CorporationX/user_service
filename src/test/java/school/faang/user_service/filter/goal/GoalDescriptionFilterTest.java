package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GoalDescriptionFilterTest {

    GoalDescriptionFilter goalDescriptionFilter = new GoalDescriptionFilter();
    GoalFilterDto goalFilterDto = new GoalFilterDto();
    List<Goal> goals;

    @BeforeEach
    void setUp() {
        Goal goalFirst = Goal.builder()
                .title("first")
                .description("description")
                .build();
        Goal goalSecond = Goal.builder()
                .title("second")
                .description("description")
                .build();
        goals = List.of(goalFirst, goalSecond);
    }

    @Test
    void testIsApplicableWhenGoalDescriptionPatternExist() {
        goalFilterDto.setDescriptionPattern("des");
        assertTrue(goalDescriptionFilter.isApplicable(goalFilterDto));
    }

    @Test
    void testIsApplicableWhenGoalDescriptionPatternNotExist() {
        assertFalse(goalDescriptionFilter.isApplicable(goalFilterDto));
    }

    @Test
    void testApplyWhenGoalDescriptionPatternExistAndDescriptionContainsIt() {
        goalFilterDto.setDescriptionPattern("des");
        Stream<Goal> result = goalDescriptionFilter.apply(goals.stream(), goalFilterDto);
        assertEquals(goals, result.toList());
    }

    @Test
    void testApplyWhenGoalDescriptionPatternExistAndDescriptionNotContainsIt() {
        goalFilterDto.setDescriptionPattern("fail");
        List<Goal> result = goalDescriptionFilter.apply(goals.stream(), goalFilterDto).toList();
        assertNotEquals(goals, result);
        assertEquals(0, result.size());
    }
}