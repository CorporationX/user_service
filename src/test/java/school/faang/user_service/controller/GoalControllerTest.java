package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.GoalService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    @Mock
    private GoalService goalService;
    @InjectMocks
    private GoalController goalController;

    @Test
    void testControllerWhenReceivedGoalWithBlankTitle() {
        Goal goal = init("");
        assertThrows(DataValidationException.class, () -> goalController.createGoal(1L, goal));
    }

    @Test
    void testControllerWhenAllCorrect() {
        Goal goal = init("Test");

        goalController.createGoal(1L, goal);

        Mockito.verify(goalService, Mockito.times(1)).createGoal(1L, goal);
    }

    private Goal init(String title) {
        Goal goal = new Goal();
        goal.setTitle(title);
        return goal;
    }
}