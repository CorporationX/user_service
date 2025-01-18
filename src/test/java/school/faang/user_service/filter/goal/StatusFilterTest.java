package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusFilterTest {

    private final StatusFilter statusFilter = new StatusFilter();

    @Test
    void isApplicable_ShouldReturnTrue_WhenStatusIsNotNull() {
        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setStatus(GoalStatus.ACTIVE);

        assertTrue(statusFilter.isApplicable(filterDto));
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenStatusIsNull() {
        GoalFilterDto filterDto = new GoalFilterDto();

        assertFalse(statusFilter.isApplicable(filterDto));
    }

    @Test
    void apply_ShouldReturnTrue_WhenGoalStatusMatchesFilterStatus() {
        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setStatus(GoalStatus.ACTIVE);

        Goal goal = new Goal();
        goal.setStatus(GoalStatus.ACTIVE);

        assertTrue(statusFilter.apply(filterDto, goal));
    }

    @Test
    void apply_ShouldReturnFalse_WhenGoalStatusDoesNotMatchFilterStatus() {
        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setStatus(GoalStatus.ACTIVE);

        Goal goal = new Goal();
        goal.setStatus(GoalStatus.COMPLETED);

        assertFalse(statusFilter.apply(filterDto, goal));
    }
}