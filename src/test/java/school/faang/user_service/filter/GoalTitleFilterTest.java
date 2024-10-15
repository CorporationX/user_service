package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.dto.goal.GoalFilterDto;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.filter.goal.GoalTitleFilter;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class GoalTitleFilterTest {
    private GoalTitleFilter titleFilter = new GoalTitleFilter();
    private Goal goal;

    @BeforeEach
    public void setUp() {
        goal = new Goal();
        goal.setTitle("Lol");
    }

    @Test
    public void givenValidUserWhenApplyThenReturnGoal() {
        // Given
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle("Lol");

        // When
        var result = titleFilter.apply(Stream.of(goal), filter);

        // Then
        assertNotNull(result);
        assertEquals(result.findFirst().get(), goal);
    }

    @Test
    public void givenValidUserWhenApplyThenReturnEmptyStream() {
        // Given
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle("Wow");

        // When
        var result = titleFilter.apply(Stream.of(goal), filter);

        // Then
        assertNotNull(result);
        assertFalse(result.findAny().isPresent());
    }
}
