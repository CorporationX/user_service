package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private static final String DESCRIPTION = "Test description";
    private static final long GOAL_ID = 1L;
    private static final long INVALID_GOAL_ID = 0L;
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
                .title(TITLE)
                .description(DESCRIPTION)
                .status(GoalStatus.ACTIVE)
                .skillIds(List.of())
                .build();
    }

    @Nested
    @DisplayName("Create Goal Tests")
    class CreateGoalTests {

        @Test
        @DisplayName("Successfully create goal with valid input")
        void whenValidInputThenCreateGoalSuccessfully() {
            when(goalService.createGoal(anyLong(), any(GoalDto.class))).thenReturn(goalDto);

            GoalDto createdGoal = goalController.createGoal(userId, goalDto);

            verify(goalService).createGoal(userId, goalDto);
            assertNotNull(createdGoal, "Created goal should not be null");
            assertEquals(goalDto.getId(), createdGoal.getId());
            assertEquals(TITLE, createdGoal.getTitle());
        }

        @Test
        @DisplayName("Throws DataValidationException for invalid input")
        void shouldThrowDataValidationExceptionForInvalidInput() {
            GoalDto invalidGoalDto = GoalDto.builder()
                    .id(INVALID_GOAL_ID)
                    .title("")
                    .description("")
                    .status(GoalStatus.ACTIVE)
                    .skillIds(List.of())
                    .build();
            String exceptionMessage = "Invalid input";

            doThrow(new DataValidationException(exceptionMessage))
                    .when(goalService)
                    .createGoal(anyLong(), eq(invalidGoalDto));

            DataValidationException exception = assertThrows(
                    DataValidationException.class,
                    () -> goalController.createGoal(userId, invalidGoalDto)
            );

            assertEquals(exceptionMessage, exception.getMessage());
            verify(goalService).createGoal(userId, invalidGoalDto);
        }
    }

    @Nested
    @DisplayName("Update Goal Tests")
    class UpdateGoalTests {

        @Test
        @DisplayName("Successfully update goal with valid input")
        void whenValidInputThenUpdateGoalSuccessfully() {
            when(goalService.updateGoal(anyLong(), any(GoalDto.class))).thenReturn(goalDto);

            GoalDto updatedGoal = goalController.updateGoal(goalId, goalDto);

            verify(goalService).updateGoal(goalId, goalDto);
            assertNotNull(updatedGoal, "Updated goal should not be null");
            assertEquals(goalDto.getId(), updatedGoal.getId());
            assertEquals(TITLE, updatedGoal.getTitle());
        }

        @Test
        @DisplayName("Throws NotFoundException when the goal does not exist")
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
            verify(goalService).updateGoal(goalId, goalDto);
        }
    }

    @Nested
    @DisplayName("Delete Goal Tests")
    class DeleteGoalTests {

        @Test
        @DisplayName("Successfully deletes goal when it exists")
        void whenGoalExistsThenDeleteSuccessfully() {
            doNothing().when(goalService).deleteGoal(goalId);

            goalController.deleteGoal(goalId);

            verify(goalService).deleteGoal(goalId);
        }

        @Test
        @DisplayName("Throws NotFoundException when the goal does not exist")
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
            verify(goalService).deleteGoal(goalId);
        }
    }

    @Nested
    @DisplayName("Fetch Goals and Subtasks Tests")
    class FetchGoalsTests {

        @Test
        @DisplayName("Fetches subtasks when a goal ID is provided")
        void whenGoalIdProvidedThenFetchSubtasks() {
            List<GoalDto> subtasks = List.of(goalDto);

            when(goalService.findSubtasksByGoalId(goalId, null, null, null, null)).thenReturn(subtasks);

            List<GoalDto> result = goalController.findSubtasksByGoalId(goalId, null, null, null, null);

            verify(goalService).findSubtasksByGoalId(goalId, null, null, null, null);
            assertNotNull(result, "Result should not be null");
            assertEquals(1, result.size());
            assertEquals(goalDto.getId(), result.get(0).getId());
        }

        @Test
        @DisplayName("Fetches goals when a user ID is provided")
        void whenUserIdProvidedThenFetchGoals() {
            List<GoalDto> goals = List.of(goalDto);

            when(goalService.getGoalsByUser(userId, null, null, null, null)).thenReturn(goals);

            List<GoalDto> result = goalController.getGoalsByUser(userId, null, null, null, null);

            verify(goalService).getGoalsByUser(userId, null, null, null, null);
            assertNotNull(result, "Result should not be null");
            assertEquals(1, result.size());
            assertEquals(goalDto.getId(), result.get(0).getId());
        }

        @Test
        @DisplayName("Returns an empty list when no subtasks are found")
        void whenNoSubtasksFoundThenReturnEmptyList() {
            when(goalService.findSubtasksByGoalId(goalId, null, null, null, null)).thenReturn(new ArrayList<>());

            List<GoalDto> result = goalController.findSubtasksByGoalId(goalId, null, null, null, null);

            verify(goalService).findSubtasksByGoalId(goalId, null, null, null, null);
            assertTrue(result.isEmpty(), "Expected empty list of subtasks");
        }

        @Test
        @DisplayName("Returns an empty list when no goals are found for the user")
        void whenNoGoalsForUserFoundThenReturnEmptyList() {
            when(goalService.getGoalsByUser(userId, null, null, null, null)).thenReturn(new ArrayList<>());

            List<GoalDto> result = goalController.getGoalsByUser(userId, null, null, null, null);

            verify(goalService).getGoalsByUser(userId, null, null, null, null);
            assertTrue(result.isEmpty(), "Expected empty list of goals");
        }
    }
}
