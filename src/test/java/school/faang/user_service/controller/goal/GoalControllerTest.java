package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {
    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    private Long userId;
    private Goal goal;


    @Test
    void blankTitleTest() {
        userId = 1L;
        goal = new Goal();
        goal.setTitle("");

        goalController.createGoal(userId, goal);
        Mockito.verify(goalService, Mockito.never()).createGoal(userId, goal);
    }

    @Test
    void nullTitleTest() {
        userId = 1L;
        goal = new Goal();

        goalController.createGoal(userId, goal);
        Mockito.verify(goalService, Mockito.never()).createGoal(userId, goal);
    }

    @Test
    void shouldCreateGoal() {
        userId = 1L;
        goal = new Goal();
        goal.setTitle("Title");

        goalController.createGoal(userId, goal);
        Mockito.verify(goalService, Mockito.times(1)).createGoal(userId, goal);
    }
}