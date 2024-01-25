package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.stream.Stream;

class GoalStatusFilterTest {
    private final GoalStatusFilter goalStatusFilter = new GoalStatusFilter();
    List<Goal> goals;

    @BeforeEach
    public void init() {
        goals = List.of(
                Goal.builder()
                        .status(GoalStatus.ACTIVE)
                        .build(),
                Goal.builder()
                        .status(GoalStatus.COMPLETED)
                        .build(),
                Goal.builder()
                        .status(GoalStatus.ACTIVE)
                        .build()
        );
    }

    @Test
    public void testReturnIsTrueIfFilterIsSpecified() {
        GoalFilterDto filterDto = GoalFilterDto.builder()
                .goalStatus(GoalStatus.ACTIVE)
                .build();

        boolean isApplicable = goalStatusFilter.isApplicable(filterDto);

        Assertions.assertTrue(isApplicable);
    }

    @Test
    public void testReturnIsFalseIfFilterIsSpecified() {
        GoalFilterDto filterDto = new GoalFilterDto();

        boolean isApplicable = goalStatusFilter.isApplicable(filterDto);
        Assertions.assertFalse(isApplicable);
    }

    @Test
    public void testReturnFilteredGoalsList() {
        GoalFilterDto filterDto = GoalFilterDto.builder()
                .goalStatus(GoalStatus.COMPLETED)
                .build();

        List<Goal> expectedGoals = List.of(
                Goal.builder()
                        .status(GoalStatus.COMPLETED)
                        .build()
        );

        Stream<Goal> tempGoals = goalStatusFilter.applyFilter(goals.stream(), filterDto);
        List<Goal> actualGoals = tempGoals.toList();

        Assertions.assertTrue(expectedGoals.size() == actualGoals.size()
                && expectedGoals.containsAll(actualGoals));
    }
}