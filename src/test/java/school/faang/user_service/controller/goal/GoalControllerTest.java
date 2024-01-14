package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.GoalValidator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Ilia Chuvatkin
 */

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {
    @Mock
    private GoalService goalService;
    @Mock
    private GoalValidator goalValidator;
    @InjectMocks
    private GoalController goalController;

    @Test
    void testCreateGoal() {
        Goal goal = new Goal();
        Long userId = 1L;

        when(goalValidator.isValidateByEmptyTitle(goal)).thenReturn(true);

        goalController.createGoal(userId,goal);

        verify(goalService,times(1)).createGoal(userId,goal);
    }
}
