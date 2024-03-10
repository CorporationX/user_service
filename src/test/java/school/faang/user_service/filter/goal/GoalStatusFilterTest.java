package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class GoalStatusFilterTest {
    @InjectMocks
    private GoalStatusFilter goalStatusFilter;
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
    void testReturnIsTrueIfFilterIsSpecified() {
        GoalFilterDto filterDto = GoalFilterDto.builder()
                .goalStatus(GoalStatus.ACTIVE)
                .build();

        boolean isApplicable = goalStatusFilter.isApplicable(filterDto);

        Assertions.assertTrue(isApplicable);
    }

    @Test
    void testReturnIsFalseIfFilterIsSpecified() {
        GoalFilterDto filterDto = new GoalFilterDto();

        boolean isApplicable = goalStatusFilter.isApplicable(filterDto);
        Assertions.assertFalse(isApplicable);
    }

    @Test
    void testReturnFilteredGoalsList() {
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