package school.faang.user_service.service.goal;


import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.goal.GoalDto;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {
    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    private Long userId;
    private GoalDto goal;


    @BeforeEach
    public void setUp() {
        userId = 1L;
        goal = new GoalDto();
        goal.setTitle("Learning");
    }


    @Test
    @DisplayName("Empty or null goal title")
    public void testCreateGoalIsInvalid() {
        GoalDto nullTitleGoal = new GoalDto();
        nullTitleGoal.setTitle(null);

        GoalDto emptyTitleGoal = new GoalDto();
        emptyTitleGoal.setTitle("    ");

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> goalController.createGoal(userId, nullTitleGoal));

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> goalController.createGoal(userId, emptyTitleGoal));
    }

    @Test
    @DisplayName("Success create new goal in controller")
    public void testCreateGoalIsSuccess() {
        goalController.createGoal(userId, goal);
        Mockito.verify(goalService, Mockito.times(1)).createGoal(userId, goal);
    }

    @Test
    @DisplayName("Success update goal in controller")
    public void testUpdateGoalIsSuccess() {
        goalController.updateGoal(userId, goal);
        Mockito.verify(goalService, Mockito.times(1)).updateGoal(userId, goal);
    }
}
