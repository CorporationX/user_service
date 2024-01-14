package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.goal.GoalService;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {
    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    private long userId;

    @Test
    void testShouldDeleteFromService() {
        userId = 1;
        goalController.deleteGoal(userId);
        Mockito.verify(goalService, Mockito.times(1)).deleteGoal(userId);
    }
}