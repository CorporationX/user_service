package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.GoalValidator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {

    @InjectMocks
    private GoalController goalController;

    @Mock
    private GoalService goalService;

    @Mock
    private GoalValidator goalValidator;

    @Test
    public void testCreateGoal() {
        GoalDto goalDto = new GoalDto();

        goalController.createGoal(1L, goalDto);
        verify(goalService, times(1)).createGoal(1L, goalDto);
    }

    @Test
    public void testUpdateGoal() {
        GoalDto goalDto = new GoalDto();

        goalController.updateGoal(1L, goalDto);
        verify(goalService, times(1)).updateGoal(1L, goalDto);
    }

    @Test
    public void testDeleteGoal() {
        goalController.deleteGoal(1L);
        verify(goalService, times(1)).deleteGoal(1L);
    }

    @Test
    public void testGetGoalsById() {
        GoalFilterDto goalFilterDto = new GoalFilterDto();
        goalController.getGoalsByUser(1L, goalFilterDto);
        verify(goalService, times(1)).findGoalsByUserId(1L, goalFilterDto);
    }

    @Test
    public void testGetSubtasksByGoal() {
        GoalFilterDto goalFilterDto = new GoalFilterDto();
        goalController.getSubtasksByGoal(1L, goalFilterDto);
        verify(goalService, times(1)).findSubtasksByGoalId(1L, goalFilterDto);
    }
}
