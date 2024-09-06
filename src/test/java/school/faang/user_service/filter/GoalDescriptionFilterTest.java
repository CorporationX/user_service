package school.faang.user_service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GoalDescriptionFilterTest {
    private GoalDescriptionFilter goalDescriptionFilter;
    private GoalFilterDto goalFilterDto;
    private Goal goal;
    private Goal anotherGoal;

    @BeforeEach
    void setUp() {
        goalDescriptionFilter = new GoalDescriptionFilter();
        goalFilterDto = new GoalFilterDto();
        goal = new Goal();
        anotherGoal = new Goal();
    }

    @Test
    public void testIsApplicableWithEmptyFilter() {
        boolean result = goalDescriptionFilter.isApplicable(goalFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableWithValidFilter() {
        goalFilterDto.setDescriptionPattern("some description");

        boolean result = goalDescriptionFilter.isApplicable(goalFilterDto);

        assertTrue(result);
    }

    @Test
    public void testApplyFilter() {
        goalFilterDto.setDescriptionPattern("some description");
        goal.setDescription("some description in goal");
        anotherGoal.setDescription("description in another goal");

        List<Goal> result = goalDescriptionFilter.apply(goalFilterDto, Stream.of(goal, anotherGoal));

        assertEquals(1, result.size());
        assertEquals(goal, result.get(0));
    }
}
