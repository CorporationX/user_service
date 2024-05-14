package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validation.goal.GoalValidation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {
    @Mock
    private GoalService goalService;
    @Mock
    private GoalValidation goalValidator;
    @InjectMocks
    private GoalController goalController;

    @Test
    void testCreateGoal() {
        GoalDto goalDto = new GoalDto();
        Long userId = 1L;

        goalController.createGoal(userId,goalDto);

        verify(goalService, times(1)).createGoal(userId, goalDto);
    }
    @Test
    void testUpdateGoal() {
        GoalDto goalDto = new GoalDto();
        Long goalId = 1L;

        goalController.updateGoal(goalId, goalDto);

        verify(goalService, times(1)).updateGoal(goalId, goalDto);
    }

    @Test
    void testDeleteGoal() {
        goalController.deleteGoal(1L);
        verify(goalService, times(1)).deleteGoal(1L);
    }

    @Test
    void testGetGoalsByUser() {
        Long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        goalController.getGoalsByUser(userId, filter);
        verify(goalService, times(1)).findGoalsByUser(userId, filter);
    }

    @Test
    void testFindSubtasksByGoalId() {
        long goalId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        goalController.findSubtasksByGoalId(goalId, filter);
        verify(goalService, times(1)).findSubtasksByGoalId(goalId, filter);
    }
}
