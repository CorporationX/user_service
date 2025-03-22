package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GoalStatusFilterTest {
    private final GoalStatusFilter goalStatusFilter = new GoalStatusFilter();

    @Test
    public void testIsApplicableTrue() {
        boolean result = goalStatusFilter.isApplicable(new GoalFilterDto(null, GoalStatus.ACTIVE));
        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = goalStatusFilter.isApplicable(new GoalFilterDto(null, null));
        assertFalse(result);
    }

    @Test
    public void testApplyOneActiveGoal() {
        Stream<Goal> goals = Stream.of(
                Goal.builder()
                        .title(null).status(GoalStatus.ACTIVE).build(),
                        Goal.builder().title(null).status(GoalStatus.COMPLETED).build()
        );
        List<Goal> goalList = goalStatusFilter.apply(goals, new GoalFilterDto(null, GoalStatus.ACTIVE)).toList();

        assertEquals(1, goalList.size());
        assertEquals(GoalStatus.ACTIVE, goalList.get(0).getStatus());
    }

    @Test
    public void testApplyTwoActiveGoals() {
        Stream<Goal> goals = Stream.of(
                Goal.builder()
                        .title(null).status(GoalStatus.ACTIVE).build(),
                Goal.builder().title(null).status(GoalStatus.ACTIVE).build()
        );
        List<Goal> goalList = goalStatusFilter.apply(goals, new GoalFilterDto(null, GoalStatus.ACTIVE)).toList();

        assertEquals(2, goalList.size());
        assertEquals(GoalStatus.ACTIVE, goalList.get(0).getStatus());
        assertEquals(GoalStatus.ACTIVE, goalList.get(1).getStatus());
    }

    @Test
    public void testApplyNoSuitableGoals() {
        Stream<Goal> goals = Stream.of(
                Goal.builder()
                        .title(null).status(GoalStatus.ACTIVE).build(),
                Goal.builder().title(null).status(GoalStatus.ACTIVE).build()
        );
        List<Goal> goalList = goalStatusFilter.apply(goals, new GoalFilterDto(null, GoalStatus.COMPLETED)).toList();

        assertTrue(goalList.isEmpty());
    }
}
