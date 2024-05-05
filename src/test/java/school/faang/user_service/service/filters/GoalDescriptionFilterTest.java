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
class GoalDescriptionFilterTest {

    private GoalDescriptionFilter goalDescriptionFilter;
    @BeforeEach
    void init() {
        goalDescriptionFilter = new GoalDescriptionFilter();
    }
    @Test
    void testIsApplicableWhenDescriptionIsBlank() {
        GoalFilterDto goalFilterDto = initFilterDto("");

        assertFalse(goalDescriptionFilter.isApplicable(goalFilterDto));
    }

    @Test
    void testIsApplicableWhenDescriptionExist() {
        GoalFilterDto goalFilterDto = initFilterDto("Test");

        assertTrue(goalDescriptionFilter.isApplicable(goalFilterDto));
    }

    @Test
    void testApply() {
        Goal firstGoal = new Goal();
        firstGoal.setDescription("Descr");
        Goal secondGoal = new Goal();
        secondGoal.setDescription("Test");
        Stream<Goal> goals = Stream.of(firstGoal, secondGoal);

        GoalFilterDto goalFilterDto = initFilterDto("Test");

        assertEquals(secondGoal, goalDescriptionFilter.apply(goals, goalFilterDto).toList().get(0));
    }

    private GoalFilterDto initFilterDto(String descr) {
        GoalFilterDto goalFilterDto = new GoalFilterDto();
        goalFilterDto.setDescriptionPattern(descr);
        return goalFilterDto;
    }
}