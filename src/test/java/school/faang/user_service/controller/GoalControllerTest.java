package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
    private static final long GOAL_ID = 1L;
    private static final long USER_ID = 1L;

    @InjectMocks
    private GoalController goalController;

    @Mock
    private GoalService goalService;

    private long goalId;
    private long userId;
    private GoalDto goalDto;

    @BeforeEach
    public void setUp() {
        goalId = GOAL_ID;
        userId = USER_ID;
        goalDto = GoalDto.builder()
                .id(GOAL_ID)
                .description("Test description")
                .title(TITLE)
                .status(GoalStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("Create Goal Tests")
    class CreateGoalTests {

        @Test
        @DisplayName("whenValidInputThenCreateGoalSuccessfully")
        void whenValidInputThenCreateGoalSuccessfully() {
            when(goalService.createGoal(anyLong(), any(GoalDto.class))).thenReturn(goalDto);

            GoalDto createdGoal = goalController.createGoal(userId, goalDto);

            verify(goalService, times(1)).createGoal(userId, goalDto);
            assertNotNull(createdGoal, "Created goal should not be null");
            assertEquals(goalDto.getId(), createdGoal.getId());
            assertEquals(TITLE, createdGoal.getTitle());
        }

        @Test
        @DisplayName("whenInvalidInputThenThrowDataValidationException")
        void whenInvalidInputThenThrowDataValidationException() {
            GoalDto invalidGoalDto = GoalDto.builder().build();
            String exceptionMessage = "Invalid input";

            doThrow(new DataValidationException(exceptionMessage))
                    .when(goalService)
                    .createGoal(anyLong(), eq(invalidGoalDto));

            DataValidationException exception = assertThrows(
                    DataValidationException.class,
                    () -> goalController.createGoal(userId, invalidGoalDto)
            );

            assertEquals(exceptionMessage, exception.getMessage());
            verify(goalService, times(1)).createGoal(userId, invalidGoalDto);
        }
    }

    @Nested
    @DisplayName("Update Goal Tests")
    class UpdateGoalTests {

        @Test
        @DisplayName("whenValidInputThenUpdateGoalSuccessfully")
        void whenValidInputThenUpdateGoalSuccessfully() {
            when(goalService.updateGoal(anyLong(), any(GoalDto.class))).thenReturn(goalDto);

            GoalDto updatedGoal = goalController.updateGoal(goalId, goalDto);

            verify(goalService, times(1)).updateGoal(goalId, goalDto);
            assertNotNull(updatedGoal, "Updated goal should not be null");
            assertEquals(goalDto.getId(), updatedGoal.getId());
            assertEquals(TITLE, updatedGoal.getTitle());
        }

        @Test
        @DisplayName("whenGoalDoesNotExistThenThrowNotFoundException")
        void whenGoalDoesNotExistThenThrowNotFoundException() {
            String exceptionMessage = "Goal not found";

            doThrow(new NotFoundException(exceptionMessage))
                    .when(goalService)
                    .updateGoal(anyLong(), any(GoalDto.class));

            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> goalController.updateGoal(goalId, goalDto)
            );

            assertEquals(exceptionMessage, exception.getMessage());
            verify(goalService, times(1)).updateGoal(goalId, goalDto);
        }
    }

    @Nested
    @DisplayName("Delete Goal Tests")
    class DeleteGoalTests {

        @Test
        @DisplayName("whenGoalExistsThenDeleteSuccessfully")
        void whenGoalExistsThenDeleteSuccessfully() {
            doNothing().when(goalService).deleteGoal(goalId);

            goalController.deleteGoal(goalId);

            verify(goalService, times(1)).deleteGoal(goalId);
        }

        @Test
        @DisplayName("whenGoalDoesNotExistThenThrowNotFoundException")
        void whenGoalDoesNotExistThenThrowNotFoundException() {
            String exceptionMessage = "Goal not found";

            doThrow(new NotFoundException(exceptionMessage))
                    .when(goalService)
                    .deleteGoal(anyLong());

            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> goalController.deleteGoal(goalId)
            );

            assertEquals(exceptionMessage, exception.getMessage());
            verify(goalService, times(1)).deleteGoal(goalId);
        }
    }

    @Nested
    @DisplayName("Fetch Goals and Subtasks Tests")
    class FetchGoalsTests {

        @Test
        @DisplayName("whenGoalIdProvidedThenFetchSubtasks")
        void whenGoalIdProvidedThenFetchSubtasks() {
            List<GoalDto> subtasks = List.of(goalDto);
            GoalFilterDto filter = new GoalFilterDto();

            when(goalService.findSubtasksByGoalId(goalId, filter)).thenReturn(subtasks);

            List<GoalDto> result = goalController.findSubtasksByGoalId(goalId, filter);

            verify(goalService, times(1)).findSubtasksByGoalId(goalId, filter);
            assertNotNull(result, "Result should not be null");
            assertEquals(1, result.size());
            assertEquals(goalDto.getId(), result.get(0).getId());
        }

        @Test
        @DisplayName("whenUserIdProvidedThenFetchGoals")
        void whenUserIdProvidedThenFetchGoals() {
            List<GoalDto> goals = List.of(goalDto);
            GoalFilterDto filter = new GoalFilterDto();

            when(goalService.getGoalsByUser(userId, filter)).thenReturn(goals);

            List<GoalDto> result = goalController.getGoalsByUser(userId, filter);

            verify(goalService, times(1)).getGoalsByUser(userId, filter);
            assertNotNull(result, "Result should not be null");
            assertEquals(1, result.size());
            assertEquals(goalDto.getId(), result.get(0).getId());
        }

        @Test
        @DisplayName("whenNoSubtasksFoundThenReturnEmptyList")
        void whenNoSubtasksFoundThenReturnEmptyList() {
            GoalFilterDto filter = new GoalFilterDto();

            when(goalService.findSubtasksByGoalId(goalId, filter)).thenReturn(new ArrayList<>());

            List<GoalDto> result = goalController.findSubtasksByGoalId(goalId, filter);

            verify(goalService, times(1)).findSubtasksByGoalId(goalId, filter);
            assertTrue(result.isEmpty(), "Expected empty list of subtasks");
        }

        @Test
        @DisplayName("whenNoGoalsForUserFoundThenReturnEmptyList")
        void whenNoGoalsForUserFoundThenReturnEmptyList() {
            GoalFilterDto filter = new GoalFilterDto();

            when(goalService.getGoalsByUser(userId, filter)).thenReturn(new ArrayList<>());

            List<GoalDto> result = goalController.getGoalsByUser(userId, filter);

            verify(goalService, times(1)).getGoalsByUser(userId, filter);
            assertTrue(result.isEmpty(), "Expected empty list of goals");
        }
    }
}
