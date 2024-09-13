package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalStatusFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
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

    @Nested
    @DisplayName("isApplicable Method Tests")
    class IsApplicableTests {

        @Test
        @DisplayName("whenStatusIsPresentThenReturnTrue")
        void whenStatusIsPresentThenReturnTrue() {
            goalFilterDto.setStatus(GoalStatus.ACTIVE);

            boolean result = goalStatusFilter.isApplicable(goalFilterDto);

            assertTrue(result, "Filter should be applicable when status is provided");
        }

        @Test
        @DisplayName("whenStatusIsAbsentThenReturnFalse")
        void whenStatusIsAbsentThenReturnFalse() {
            boolean result = goalStatusFilter.isApplicable(goalFilterDto);

            assertFalse(result, "Filter should not be applicable when status is not provided");
        }
    }

    @Nested
    @DisplayName("apply Method Tests")
    class ApplyTests {

        @Test
        @DisplayName("whenActiveStatusProvidedThenFilterActiveGoals")
        void whenActiveStatusProvidedThenFilterActiveGoals() {
            goalFilterDto.setStatus(GoalStatus.ACTIVE);

            Stream<Goal> goalsStream = Stream.of(activeGoal, completedGoal);
            List<Goal> filteredGoals = goalStatusFilter.apply(goalsStream, goalFilterDto).toList();

            assertEquals(1, filteredGoals.size(), "There should be one goal with ACTIVE status");
            assertEquals(GoalStatus.ACTIVE, filteredGoals.get(0).getStatus(), "The filtered goal should have ACTIVE status");
        }

        @Test
        @DisplayName("whenCompletedStatusProvidedThenFilterCompletedGoals")
        void whenCompletedStatusProvidedThenFilterCompletedGoals() {
            goalFilterDto.setStatus(GoalStatus.COMPLETED);

            Stream<Goal> goalsStream = Stream.of(activeGoal, completedGoal);
            List<Goal> filteredGoals = goalStatusFilter.apply(goalsStream, goalFilterDto).toList();

            assertEquals(1, filteredGoals.size(), "There should be one goal with COMPLETED status");
            assertEquals(GoalStatus.COMPLETED, filteredGoals.get(0).getStatus(), "The filtered goal should have COMPLETED status");
        }
    }
}

