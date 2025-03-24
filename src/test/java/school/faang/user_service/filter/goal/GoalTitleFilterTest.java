package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GoalTitleFilterTest {
    private final GoalTitleFilter goalTitleFilter = new GoalTitleFilter();

    @Test
    public void testIsApplicableTrue() {
        boolean result = goalTitleFilter.isApplicable(new GoalFilterDto("Title", null));
        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        boolean result = goalTitleFilter.isApplicable(new GoalFilterDto(null, null));
        assertFalse(result);
    }

    @Test
    public void testApplyOneTitle() {
        Stream<Goal> goals = Stream.of(
                Goal.builder()
                        .title("Title").status(null).build(),
                Goal.builder().title("TitleTest").status(null).build()
        );
        List<Goal> goalList = goalTitleFilter.apply(goals, new GoalFilterDto("Title", null)).toList();

        assertEquals(1, goalList.size());
        assertEquals("Title", goalList.get(0).getTitle());
    }

    @Test
    public void testApplyTwoTitle() {
        Stream<Goal> goals = Stream.of(
                Goal.builder()
                        .title("Title").status(null).build(),
                Goal.builder().title("Title").status(null).build()
        );
        List<Goal> goalList = goalTitleFilter.apply(goals, new GoalFilterDto("Title", null)).toList();

        assertEquals(2, goalList.size());
        assertEquals("Title", goalList.get(0).getTitle());
        assertEquals("Title", goalList.get(1).getTitle());
    }

    @Test
    public void testApplyNoSuitableTitle() {
        Stream<Goal> goals = Stream.of(
                Goal.builder()
                        .title("Title").status(null).build(),
                Goal.builder().title("TitleTest").status(null).build()
        );
        List<Goal> goalList = goalTitleFilter.apply(goals, new GoalFilterDto("TestTitle", null)).toList();

        assertTrue(goalList.isEmpty());
    }
}
