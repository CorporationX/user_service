package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GoalParentIdFilterTest {

    GoalParentIdFilter goalParentIdFilter = new GoalParentIdFilter();
    GoalFilterDto goalFilterDto = new GoalFilterDto();
    List<Goal> goals;

    @BeforeEach
    void setUp() {
        Goal parentGoal = Goal.builder()
                .id(1L)
                .build();
        Goal goalFirst = Goal.builder()
                .title("first")
                .description("first")
                .parent(parentGoal)
                .build();
        Goal goalSecond = Goal.builder()
                .title("second")
                .description("second")
                .parent(parentGoal)
                .build();
        goals = List.of(goalFirst, goalSecond);
    }

    @Test
    void testIsApplicableWhenGoalParentIdPatternExist() {
        goalFilterDto.setParentIdPattern(1L);
        assertTrue(goalParentIdFilter.isApplicable(goalFilterDto));
    }

    @Test
    void testIsApplicableWhenGoalParentIdPatternNotExist() {
        assertFalse(goalParentIdFilter.isApplicable(goalFilterDto));
    }

    @Test
    void testApplyWhenGoalParentIdPatternExistAndMatchesParentId() {
        goalFilterDto.setParentIdPattern(1L);
        Stream<Goal> result = goalParentIdFilter.apply(goals.stream(), goalFilterDto);
        assertEquals(goals, result.toList());
    }

    @Test
    void testApplyWhenGoalParentIdPatternExistAndNotMatchesParentId() {
        goalFilterDto.setParentIdPattern(2L);
        List<Goal> result = goalParentIdFilter.apply(goals.stream(), goalFilterDto).toList();
        assertNotEquals(goals, result);
        assertEquals(0, result.size());
    }
}