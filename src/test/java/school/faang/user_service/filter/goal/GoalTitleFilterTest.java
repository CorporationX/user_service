package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class GoalTitleFilterTest {
    @InjectMocks
    private GoalTitleFilter goalTitleFilter;
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
    public void testReturnTrueIfFilterIsSpecified() {
        GoalFilterDto filterDto = GoalFilterDto.builder()
                .title("java")
                .build();

        boolean isApplicable = goalTitleFilter.isApplicable(filterDto);

        Assertions.assertTrue(isApplicable);
    }

    @Test
    public void testReturnFalseIfFilterIsSpecified() {
        GoalFilterDto filterDto = new GoalFilterDto();

        boolean isApplicable = goalTitleFilter.isApplicable(filterDto);

        Assertions.assertFalse(isApplicable);
    }

    @Test
    public void testReturnFilteredGoalsList() {
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

        Stream<Goal> tempGoals = goalTitleFilter.applyFilter(goals.stream(), filterDto);
        List<Goal> actualGoals = tempGoals.toList();

        Assertions.assertTrue(expectedGoals.size() == actualGoals.size()
                && expectedGoals.containsAll(actualGoals));
    }
}