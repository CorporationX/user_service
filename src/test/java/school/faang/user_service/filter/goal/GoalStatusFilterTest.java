package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class GoalStatusFilterTest {

    GoalStatusFilter goalStatusFilter = new GoalStatusFilter();
    GoalFilterDto goalFilterDto = new GoalFilterDto();
    List<Goal> goals;

    @BeforeEach
    void setUp() {
        Goal goalFirst = Goal.builder()
                .title("first")
                .description("first")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goalSecond = Goal.builder()
                .title("second")
                .description("second")
                .status(GoalStatus.ACTIVE)
                .build();
        goals = List.of(goalFirst, goalSecond);
    }

    @Test
    void testIsApplicableWhenGoalStatusPatternExist() {
        goalFilterDto.setStatusPattern(GoalStatus.ACTIVE);
        assertTrue(goalStatusFilter.isApplicable(goalFilterDto));
    }

    @Test
    void testIsApplicableWhenGoalStatusPatternNotExist() {
        assertFalse(goalStatusFilter.isApplicable(goalFilterDto));
    }

    @Test
    void testApplyWhenGoalStatusPatternExistAndMatchesGoalStatus() {
        goalFilterDto.setStatusPattern(GoalStatus.ACTIVE);
        Stream<Goal> result = goalStatusFilter.apply(goals.stream(), goalFilterDto);
        assertEquals(goals, result.toList());
    }

    @Test
    void testApplyWhenGoalStatusPatternExistAndNotMatchesGoalStatus() {
        goalFilterDto.setStatusPattern(GoalStatus.COMPLETED);
        List<Goal> result = goalStatusFilter.apply(goals.stream(), goalFilterDto).toList();
        assertNotEquals(goals, result);
        assertEquals(0, result.size());
    }
}