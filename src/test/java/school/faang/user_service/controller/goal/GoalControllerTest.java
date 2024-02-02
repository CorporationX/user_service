package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {
    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    @Test
    public void testDeleteGoalWhenForNegativeValue() {
        long id = -1L;

        assertThrows(DataValidationException.class,
                () -> goalController.deleteGoal(id));
    }

    @Test
    public void testDeleteGoalForValidId() {
        goalController.deleteGoal(1L);

        Mockito.verify(goalService, times(1)).deleteGoal(1L);
    }
}