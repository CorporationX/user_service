package school.faang.user_service.service.controller.goal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.controller.goal.GoalController;
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
        goal.setTitle(" ");

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
    public void testCreateNormal() {
        Long userId = 1L;
        Goal goal = new Goal();
        goal.setTitle("title");
        when(goalService.findAllGoalTitles()).thenReturn(List.of("test"));

        goalController.createGoal(userId, goal);
        verify(goalService, times(1)).createGoal(userId, goal);
    }

}