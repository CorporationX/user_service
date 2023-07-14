package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exception.EmptyGoalsException;
import school.faang.user_service.service.goal.GoalService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTests {

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    @Test
    void testGetGoalsByUser_ValidInput() {
        Long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto();

        GoalDto expectedGoal1 = new GoalDto();
        GoalDto expectedGoal2 = new GoalDto();

        List<GoalDto> expectedGoals = Arrays.asList(expectedGoal1, expectedGoal2);

        when(goalService.getGoalsByUser(userId, filter)).thenReturn(expectedGoals);

        List<GoalDto> actualGoals = goalController.getGoalsByUser(userId, filter);

        assertEquals(expectedGoals.size(), actualGoals.size());
        assertEquals(expectedGoals, actualGoals);

        verify(goalService, times(1)).getGoalsByUser(userId, filter);
    }

    @Test
    void testGetGoalsByUser_NullUserId() {
        Long userId = null;
        GoalFilterDto filter = new GoalFilterDto();

        assertThrows(IllegalArgumentException.class, () -> {
            goalController.getGoalsByUser(userId, filter);
        });

        verify(goalService, never()).getGoalsByUser(anyLong(), any(GoalFilterDto.class));
    }

    @Test
    void testGetGoalsByUser_NullFilter() {
        Long userId = 1L;
        GoalFilterDto filter = null;

        assertThrows(IllegalArgumentException.class, () -> {
            goalController.getGoalsByUser(userId, filter);
        });

        verify(goalService, never()).getGoalsByUser(anyLong(), any(GoalFilterDto.class));
    }

    @Test
    void testGetGoalsByUser_EmptyGoalsList() {
        Long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto();

        when(goalService.getGoalsByUser(userId, filter)).thenReturn(Collections.emptyList());

        assertThrows(EmptyGoalsException.class, () -> {
            goalController.getGoalsByUser(userId, filter);
        });

        verify(goalService, times(1)).getGoalsByUser(userId, filter);
    }
}