package school.faang.user_service.service.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalStatusFilterTest {

    private GoalStatusFilter goalStatusFilter;
    @BeforeEach
    void init() {
        goalStatusFilter = new GoalStatusFilter();
    }
    @Test
    void testIsApplicableWhenStatusIsBlank() {
        GoalFilterDto goalFilterDto = initFilterDto("");

        assertFalse(goalStatusFilter.isApplicable(goalFilterDto));
    }

    @Test
    void testIsApplicableWhenStatusNotBlank() {
        GoalFilterDto goalFilterDto = initFilterDto("ACTIVE");

        assertTrue(goalStatusFilter.isApplicable(goalFilterDto));
    }

    @Test
    void testApply() {
        Goal firstGoal = new Goal();
        firstGoal.setStatus(GoalStatus.COMPLETED);
        Goal secondGoal = new Goal();
        secondGoal.setStatus(GoalStatus.ACTIVE);
        Stream<Goal> goals = Stream.of(firstGoal, secondGoal);

        GoalFilterDto goalFilterDto = initFilterDto("ACTIVE");

        assertEquals(secondGoal, goalStatusFilter.apply(goals, goalFilterDto).toList().get(0));
    }

    private GoalFilterDto initFilterDto(String status) {
        GoalFilterDto goalFilterDto = new GoalFilterDto();
        goalFilterDto.setStatusPattern(status);
        return goalFilterDto;
    }
}