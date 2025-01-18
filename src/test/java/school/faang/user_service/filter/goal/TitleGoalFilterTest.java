package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TitleGoalFilterTest {

    private TitleGoalFilter titleGoalFilter;

    @BeforeEach
    void setUp() {
        titleGoalFilter = new TitleGoalFilter();
    }

    @Test
    void isApplicable_ShouldReturnTrue_WhenTitleIsPresent() {
        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setTitle("Test Title");

        assertTrue(titleGoalFilter.isApplicable(filterDto));
    }

    @Test
    void isApplicable_ShouldReturnFalse_WhenTitleIsNotPresent() {
        GoalFilterDto filterDto = new GoalFilterDto();

        assertFalse(titleGoalFilter.isApplicable(filterDto));
    }

    @Test
    void apply_ShouldReturnTrue_WhenGoalTitleMatches() {
        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setTitle("Test Title");

        Goal goal = new Goal();
        goal.setTitle("Test Title");

        assertTrue(titleGoalFilter.apply(filterDto, goal));
    }

    @Test
    void apply_ShouldReturnFalse_WhenGoalTitleDoesNotMatch() {
        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setTitle("Test Title");

        Goal goal = new Goal();
        goal.setTitle("Different Title");

        assertFalse(titleGoalFilter.apply(filterDto, goal));
    }
}