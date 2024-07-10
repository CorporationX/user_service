package school.faang.user_service.service.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.GoalControllerValidate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GoalControllerTest {

    @InjectMocks
    private GoalController goalController;

    @Mock
    private GoalService goalService;
    @Mock
    private GoalControllerValidate validate;

    @DisplayName("Если title пустой")
    @Test
    public void testCreateWithBlankTitle() {
        Goal goal = new Goal();
        goal.setTitle("");

        assertThrows(IllegalArgumentException.class, () -> goalController.createGoal(1L, goal));
    }

    @DisplayName("Если title слишком длинный")
    @Test
    public void testCreateMaxLengthTitle() {
        Goal goal = new Goal();
        goal.setTitle("fdsfsdfsfdskjofdshjfdsjfjasdklfjsdakljfklsdajflksdajfklsdjfklsdkkk");

        assertThrows(IllegalArgumentException.class, () -> goalController.createGoal(1L, goal));
    }

    @Test
    public void testCreateWhenValid() {
        Long userId = 1L;
        Goal goal = new Goal();

        goalController.createGoal(userId, goal);
        verify(goalService, times(1)).createGoal(userId, goal);
    }

    @Test
    public void testUpdateGoalNullGoal() {
        long goalId = 1L;
        GoalDto goal = null;

        verify(goalService, times(0)).updateGoal(goalId, goal);
    }

    @Test
    public void testUpdateGoalWhenValid() {
        long goalId = 1L;
        GoalDto goal = new GoalDto();

        goalController.updateGoal(goalId, goal);
        verify(validate, times(1)).validateId(goalId);
        verify(goalService, times(1)).updateGoal(goalId, goal);
    }

    @Test
    public void testDeleteGoalWhenValid() {
        long goalId = 1L;

        goalController.deleteGoal(goalId);
        verify(validate, times(1)).validateId(goalId);
        verify(goalService, times(1)).deleteGoal(goalId);
    }

    @Test
    public void testGetSubtasksByGoalIdWhenValid() {
        long goalId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        goalController.getSubtasksByGoalId(goalId, filters);
        verify(validate, times(1)).validateId(goalId);
        verify(goalService, times(1)).getSubtasksByGoalId(goalId, filters);
    }

    @Test
    public void testGetGoalsByUserWhenValid() {
        long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        goalController.getGoalsByUser(userId, filters);
        verify(validate, times(1)).validateId(userId);
        verify(goalService, times(1)).getGoalsByUser(userId, filters);
    }
}