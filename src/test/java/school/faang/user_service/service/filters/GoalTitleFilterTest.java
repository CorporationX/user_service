package school.faang.user_service.service.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalTitleFilterTest {

    private GoalTitleFilter goalTitleFilter;
    @BeforeEach
    void init() {
        goalTitleFilter = new GoalTitleFilter();
    }
    @Test
    void testIsApplicableWhenTitleIsBlank() {
        GoalFilterDto goalFilterDto = initFilterDto("");

        assertFalse(goalTitleFilter.isApplicable(goalFilterDto));
    }

    @Test
    void testIsApplicableWhenDescriptionExist() {
        GoalFilterDto goalFilterDto = initFilterDto("Test title");

        assertTrue(goalTitleFilter.isApplicable(goalFilterDto));
    }

    @Test
    void testApply() {
        Goal firstGoal = new Goal();
        firstGoal.setTitle("Something");
        Goal secondGoal = new Goal();
        secondGoal.setTitle("Test");
        Stream<Goal> goals = Stream.of(firstGoal, secondGoal);

        GoalFilterDto goalFilterDto = initFilterDto("Test");

        assertEquals(secondGoal, goalTitleFilter.apply(goals, goalFilterDto).toList().get(0));
    }

    private GoalFilterDto initFilterDto(String title) {
        GoalFilterDto goalFilterDto = new GoalFilterDto();
        goalFilterDto.setTitlePattern(title);
        return goalFilterDto;
    }
}