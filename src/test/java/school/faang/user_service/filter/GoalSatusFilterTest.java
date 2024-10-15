package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.dto.goal.GoalFilterDto;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.model.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalStatusFilter;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class GoalSatusFilterTest {
    private GoalStatusFilter statusFilter = new GoalStatusFilter();
    private Goal goal;

    @BeforeEach
    public void setUp() {
        goal = new Goal();
        goal.setStatus(GoalStatus.COMPLETED);
    }

    @Test
    public void givenValidUserWhenApplyThenReturnGoal() {
        // Given
        GoalFilterDto filter = new GoalFilterDto();
        filter.setStatus(GoalStatus.COMPLETED);

        // When
        var result = statusFilter.apply(Stream.of(goal), filter);

        // Then
        assertNotNull(result);
        assertEquals(result.findFirst().get(), goal);
    }

    @Test
    public void givenValidUserWhenApplyThenReturnEmptyStream() {
        // Given
        GoalFilterDto filter = new GoalFilterDto();
        filter.setStatus(GoalStatus.ACTIVE);

        // When
        var result = statusFilter.apply(Stream.of(goal), filter);

        // Then
        assertNotNull(result);
        assertFalse(result.findAny().isPresent());
    }
}
