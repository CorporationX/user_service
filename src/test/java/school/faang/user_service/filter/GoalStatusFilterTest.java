package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalStatusFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GoalStatusFilterTest {

    private GoalStatusFilter goalStatusFilter;
    private GoalFilterDto goalFilterDto;
    private Goal activeGoal;
    private Goal completedGoal;

    @BeforeEach
    public void setUp() {
        goalStatusFilter = new GoalStatusFilter();

        activeGoal = new Goal();
        activeGoal.setStatus(GoalStatus.ACTIVE);

        completedGoal = new Goal();
        completedGoal.setStatus(GoalStatus.COMPLETED);

        goalFilterDto = new GoalFilterDto();
    }

    @Test
    @DisplayName("Test isApplicable returns true when status is present in filters")
    public void testIsApplicableWithStatus() {
        goalFilterDto.setStatus(GoalStatus.ACTIVE);

        boolean result = goalStatusFilter.isApplicable(goalFilterDto);

        assertTrue(result, "Filter should be applicable when status is provided");
    }

    @Test
    @DisplayName("Test isApplicable returns false when status is not present in filters")
    public void testIsApplicableWithoutStatus() {
        boolean result = goalStatusFilter.isApplicable(goalFilterDto);

        assertFalse(result, "Filter should not be applicable when status is not provided");
    }

    @Test
    @DisplayName("Test apply filters goals by status")
    public void testApplyFilterByStatus() {
        goalFilterDto.setStatus(GoalStatus.ACTIVE);

        Stream<Goal> goalsStream = Stream.of(activeGoal, completedGoal);
        List<Goal> filteredGoals = goalStatusFilter.apply(goalsStream, goalFilterDto).toList();

        assertEquals(1, filteredGoals.size(), "There should be one goal with ACTIVE status");
        assertEquals(GoalStatus.ACTIVE, filteredGoals.get(0).getStatus(), "The filtered goal should have ACTIVE status");
    }

    @Test
    @DisplayName("Test apply does not filter when status is COMPLETED")
    public void testApplyFilterWithCompletedStatus() {
        goalFilterDto.setStatus(GoalStatus.COMPLETED);

        Stream<Goal> goalsStream = Stream.of(activeGoal, completedGoal);
        List<Goal> filteredGoals = goalStatusFilter.apply(goalsStream, goalFilterDto).toList();

        assertEquals(1, filteredGoals.size(), "There should be one goal with COMPLETED status");
        assertEquals(GoalStatus.COMPLETED, filteredGoals.get(0).getStatus(), "The filtered goal should have COMPLETED status");
    }
}
