package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.dto.goal.GoalFilterDto;
import school.faang.user_service.model.entity.goal.Goal;
import school.faang.user_service.filter.goal.GoalDescriptionFilter;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class GoalDescriptionFilterTest {

    private GoalDescriptionFilter descriptionFilter = new GoalDescriptionFilter();
    private Goal goal;

    @BeforeEach
    public void setUp() {
        goal = new Goal();
        goal.setDescription("Lol");
    }

    @Test
    public void givenValidUserWhenApplyThenReturnGoal() {
        // Given
        GoalFilterDto filter = new GoalFilterDto();
        filter.setDescription("Lol");

        // When
        var result = descriptionFilter.apply(Stream.of(goal), filter);

        // Then
        assertNotNull(result);
        assertEquals(result.findFirst().get(), goal);
    }

    @Test
    public void givenValidUserWhenApplyThenReturnEmptyStream() {
        // Given
        GoalFilterDto filter = new GoalFilterDto();
        filter.setDescription("Wow");

        // When
        var result = descriptionFilter.apply(Stream.of(goal), filter);

        // Then
        assertNotNull(result);
        assertFalse(result.findAny().isPresent());
    }

}
