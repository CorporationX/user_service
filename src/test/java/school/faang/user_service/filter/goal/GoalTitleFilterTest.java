package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

class GoalTitleFilterTest {
    private final GoalTitleFilter goalTitleFilter = new GoalTitleFilter();
    private List<Goal> goals;

    @BeforeEach
    public void init() {
        goals = List.of(
                Goal.builder()
                        .title("java")
                        .build(),
                Goal.builder()
                        .title("python")
                        .build(),
                Goal.builder()
                        .title("go")
                        .build(),
                Goal.builder()
                        .title("java script")
                        .build()
        );
    }

    @Test
    public void shouldReturnTrueIfFilterIsSpecified() {
        GoalFilterDto filterDto = GoalFilterDto.builder()
                .title("java")
                .build();

        boolean isApplicable = goalTitleFilter.isApplicable(filterDto);

        Assertions.assertTrue(isApplicable);
    }

    @Test
    public void shouldReturnFalseIfFilterIsSpecified() {
        GoalFilterDto filterDto = new GoalFilterDto();

        boolean isApplicable = goalTitleFilter.isApplicable(filterDto);

        Assertions.assertFalse(isApplicable);
    }

    @Test
    public void shouldReturnFilteredGoalsList() {
        GoalFilterDto filterDto = GoalFilterDto.builder()
                .title("java")
                .build();
        List<Goal> expectedGoals = List.of(
                Goal.builder()
                        .title("java")
                        .build(),
                Goal.builder()
                        .title("java script")
                        .build()
        );

        Stream<Goal> actualGoals = goalTitleFilter.applyFilter(goals.stream(), filterDto);

        Assertions.assertEquals(expectedGoals, actualGoals.toList());
    }
}