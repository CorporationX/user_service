package school.faang.user_service.service.filter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.filter.GoalFilterId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GoalFilterIdTest {
    @Autowired
    private GoalFilterId goalFilterId;

    @Test
    void testIsApplicableReturnsTrue() {
        GoalFilterDto goalFilterDto = getGoalFilterDto();
        boolean expected = true;

        boolean actual = goalFilterId.isApplicable(goalFilterDto);

        assertEquals(expected, actual);
    }

    @Test
    void testApplyFilter() {
        GoalFilterDto goalFilterDto = getGoalFilterDto();
        List<Goal> goalsActual = getGoals();
        List<Goal> goalsExpected = Arrays.asList(getGoals().get(0));

        goalFilterId.apply(goalsActual, goalFilterDto);

        assertEquals(goalsActual, goalsExpected);
    }

    private GoalFilterDto getGoalFilterDto() {
        return GoalFilterDto.builder()
                .id(1L)
                .goalStatus(GoalStatus.ACTIVE)
                .titlePattern("Title")
                .build();
    }

    private List<Goal> getGoals() {
        return new ArrayList<>(List.of(
                Goal.builder().id(1L).title("Title").status(GoalStatus.ACTIVE).build(),
                Goal.builder().id(2L).title("Title").status(GoalStatus.ACTIVE).build(),
                Goal.builder().id(3L).title("Title").status(GoalStatus.ACTIVE).build()
        ));
    }
}
