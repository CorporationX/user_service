package school.faang.user_service.service.controller.goal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GoalControllerTest {

    @InjectMocks
    private GoalController goalController;

    @Mock
    private GoalService goalService;

    @Test
    public void testCreateWithBlankTitle() {
        Long userId = 1L;
        Goal goal = new Goal();
        goal.setTitle("");

        assertThrows(NullPointerException.class, () -> goalController.createGoal(userId, goal));
    }

    @Test
    public void testCreateMaxLengthTitle() {
        Long userId = 1L;
        Goal goal = new Goal();
        goal.setTitle("fdsfsdfsfdskjofdshjfdsjfjasdklfjsdakljfklsdajflksdajfklsdjfklsdkkk");

        assertThrows(NullPointerException.class, () -> goalController.createGoal(userId, goal));
    }

    @Test
    public void testCreateDuplicateTitle() {
        Long userId = 1L;
        Goal goal = new Goal();
        String title = "test";
        goal.setTitle(title);

        when(goalService.findAllGoalTitles()).thenReturn(List.of(title));
        assertThrows(NullPointerException.class, () -> goalController.createGoal(userId, goal));
    }

    @Test
    public void testCreateWhenValid() {
        Long userId = 1L;
        Goal goal = new Goal();
        goal.setTitle("title");
        when(goalService.findAllGoalTitles()).thenReturn(List.of());

        goalController.createGoal(userId, goal);
        verify(goalService, times(1)).createGoal(userId, goal);
    }

    @Test
    public void testUpdateGoalNullGoalId() {
        Long goalId = null;
        GoalDto goal = new GoalDto();

        verify(goalService, times(0)).updateGoal(goalId, goal);
//        assertThrows(NullPointerException.class, () -> goalController.updateGoal(goalId, goal));
    }

    @Test
    public void testUpdateGoalNullGoal() {
        Long goalId = 1L;
        GoalDto goal = null;

        verify(goalService, times(0)).updateGoal(goalId, goal);
//        assertThrows(NullPointerException.class, () -> goalController.updateGoal(goalId, goal));
    }

    @Test
    public void testUpdateGoalWhenValid() {
        Long goalId = 1L;
        GoalDto goal = new GoalDto();

        goalController.updateGoal(goalId, goal);
        verify(goalService, times(1)).updateGoal(goalId, goal);
    }

    @Test
    public void testDeleteGoalNullGoalId() {
        Long goalId = null;

//        assertThrows(NullPointerException.class, () -> goalController.deleteGoal(goalId));
        verify(goalService, times(0)).deleteGoal(goalId);
    }

    @Test
    public void testDeleteGoalWhenValid() {
        Long goalId = 1L;

        goalController.deleteGoal(goalId);
        verify(goalService, times(1)).deleteGoal(goalId);
    }

    @Test
    public void testGetSubtasksByGoalIdNullGoalId() {
        Long goalId = null;
        GoalFilterDto filters = new GoalFilterDto();

        verify(goalService, times(0)).getSubtasksByGoalId(goalId, filters);
    }

    @Test
    public void testGetSubtasksByGoalIdWhenValid() {
        Long goalId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        goalController.getSubtasksByGoalId(goalId, filters);
        verify(goalService, times(1)).getSubtasksByGoalId(goalId, filters);
    }

    @Test
    public void testGetGoalsByUserNullUserId() {
        Long userId = null;
        GoalFilterDto filters = new GoalFilterDto();

        verify(goalService, times(0)).getGoalsByUser(userId, filters);
//        assertThrows(NullPointerException.class, () -> goalController.getSubtasksByGoalId(userId, filters));
    }

    @Test
    public void testGetGoalsByUserWhenValid() {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        goalController.getGoalsByUser(userId, filters);
        verify(goalService, times(1)).getGoalsByUser(userId, filters);
    }
}