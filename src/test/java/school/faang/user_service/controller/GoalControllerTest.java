package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {
    private static final String TITLE = "Test Goal";

    @InjectMocks
    private GoalController goalController;

    @Mock
    private GoalService goalService;

    private long goalId;
    private long userId;
    private GoalDto goalDto;

    @BeforeEach
    public void setUp() {
        goalId = 1L;
        userId = 1L;
        goalDto = GoalDto.builder()
                .id(1L)
                .description("Test description")
                .title(TITLE)
                .status(GoalStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Test Goal Creation with valid input")
    public void testCreateGoal() {
        when(goalService.createGoal(anyLong(), any(GoalDto.class))).thenReturn(goalDto);

        GoalDto createdGoal = goalController.createGoal(userId, goalDto);

        verify(goalService, times(1)).createGoal(userId, goalDto);

        assertNotNull(createdGoal, "Created goal should not be null");
        assertEquals(goalDto.getId(), createdGoal.getId());
        assertEquals(TITLE, createdGoal.getTitle());
    }

    @Test
    @DisplayName("Test Goal Update with valid input")
    public void testUpdateGoal() {
        when(goalService.updateGoal(anyLong(), any(GoalDto.class))).thenReturn(goalDto);

        GoalDto updatedGoal = goalController.updateGoal(goalId, goalDto);

        verify(goalService, times(1)).updateGoal(goalId, goalDto);

        assertNotNull(updatedGoal, "Updated goal should not be null");
        assertEquals(goalDto.getId(), updatedGoal.getId());
        assertEquals(TITLE, updatedGoal.getTitle());
    }

    @Test
    @DisplayName("Test Goal Deletion")
    public void testDeleteGoal() {
        doNothing().when(goalService).deleteGoal(goalId);

        goalController.deleteGoal(goalId);

        verify(goalService, times(1)).deleteGoal(goalId);
    }

    @Test
    @DisplayName("Test fetching subtasks by Goal ID with valid input")
    public void testGetSubtasksByGoalId() {
        List<GoalDto> subtasks = List.of(goalDto);

        GoalFilterDto filter = new GoalFilterDto();
        when(goalService.getSubtasksByGoalId(goalId, filter)).thenReturn(subtasks);

        List<GoalDto> result = goalController.getSubtasksByGoalId(goalId, filter);

        verify(goalService, times(1)).getSubtasksByGoalId(goalId, filter);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size());
        assertEquals(goalDto.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Test fetching Goals by User ID with valid input")
    public void testGetGoalsByUser() {
        List<GoalDto> goals = new ArrayList<>();
        goals.add(goalDto);

        GoalFilterDto filter = new GoalFilterDto();
        when(goalService.getGoalsByUser(userId, filter)).thenReturn(goals);

        List<GoalDto> result = goalController.getGoalsByUser(userId, filter);

        verify(goalService, times(1)).getGoalsByUser(userId, filter);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size());
        assertEquals(goalDto.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("Test Goal Creation with invalid input")
    public void testCreateGoalWithInvalidInput() {
        GoalDto invalidGoalDto = GoalDto.builder().build();
        String exceptionMessage = "Invalid input";

        doThrow(new DataValidationException(exceptionMessage))
                .when(goalService)
                .createGoal(anyLong(), eq(invalidGoalDto));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalController.createGoal(userId, invalidGoalDto));

        assertEquals(exceptionMessage, exception.getMessage());
        verify(goalService, times(1)).createGoal(userId, invalidGoalDto);
    }

    @Test
    @DisplayName("Test Goal Update when Goal Does Not Exist")
    public void testUpdateGoalWhenGoalDoesNotExist() {
        String exceptionMessage = "Goal not found";

        doThrow(new NotFoundException(exceptionMessage))
                .when(goalService)
                .updateGoal(anyLong(), any(GoalDto.class));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> goalController.updateGoal(goalId, goalDto));

        assertEquals(exceptionMessage, exception.getMessage());
        verify(goalService, times(1)).updateGoal(goalId, goalDto);
    }

    @Test
    @DisplayName("Test Goal Deletion when Goal Does Not Exist")
    public void testDeleteGoalWhenGoalDoesNotExist() {
        String exceptionMessage = "Goal not found";

        doThrow(new NotFoundException(exceptionMessage))
                .when(goalService)
                .deleteGoal(anyLong());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> goalController.deleteGoal(goalId));

        assertEquals(exceptionMessage, exception.getMessage());
        verify(goalService, times(1)).deleteGoal(goalId);
    }

    @Test
    @DisplayName("Test fetching subtasks by Goal ID when no data found")
    public void testGetSubtasksByGoalIdWhenNoDataFound() {
        GoalFilterDto filter = new GoalFilterDto();

        when(goalService.getSubtasksByGoalId(goalId, filter)).thenReturn(new ArrayList<>());

        List<GoalDto> result = goalController.getSubtasksByGoalId(goalId, filter);

        verify(goalService, times(1)).getSubtasksByGoalId(goalId, filter);

        assertTrue(result.isEmpty(), "Expected empty list of subtasks");
    }

    @Test
    @DisplayName("Test fetching Goals by User ID when no data found")
    public void testGetGoalsByUserWhenNoDataFound() {
        GoalFilterDto filter = new GoalFilterDto();

        when(goalService.getGoalsByUser(userId, filter)).thenReturn(new ArrayList<>());

        List<GoalDto> result = goalController.getGoalsByUser(userId, filter);

        verify(goalService, times(1)).getGoalsByUser(userId, filter);

        assertTrue(result.isEmpty(), "Expected empty list of goals");
    }
}
