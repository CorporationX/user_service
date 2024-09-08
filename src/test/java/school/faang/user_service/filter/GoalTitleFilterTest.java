package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GoalTitleFilterTest {
    private GoalTitleFilter goalTitleFilter;
    private GoalFilterDto goalFilterDto;
    private Goal goal;
    private Goal anotherGoal;

    @BeforeEach
    void setUp() {
        goalTitleFilter = new GoalTitleFilter();
        goalFilterDto = new GoalFilterDto();
        goal = new Goal();
        anotherGoal = new Goal();
    }

    @Test
    public void testIsApplicableWithEmptyFilter() {
        boolean result = goalTitleFilter.isApplicable(goalFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableWithValidFilter() {
        goalFilterDto.setTitlePattern("some title");

        boolean result = goalTitleFilter.isApplicable(goalFilterDto);

        assertTrue(result);
    }

    @Test
    public void testApplyFilter() {
        goalFilterDto.setTitlePattern("some title");
        goal.setTitle("some title");
        anotherGoal.setTitle("title");

        List<Goal> result = goalTitleFilter.apply(goalFilterDto, Stream.of(goal, anotherGoal));

        assertTrue(result.contains(goal));
        assertFalse(result.contains(anotherGoal));
    }
}
