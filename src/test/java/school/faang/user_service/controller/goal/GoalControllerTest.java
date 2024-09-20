package school.faang.user_service.controller.goal;

import jakarta.persistence.EntityNotFoundException;
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
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {

    private static final String TITLE = "Test Goal";
    private static final String DESCRIPTION = "Test description";
    private static final long GOAL_ID = 1L;
    private static final long USER_ID = 1L;

    @InjectMocks
    private GoalController goalController;

    @Mock
    private GoalService goalService;

    private GoalDto goalDto;

    @BeforeEach
    public void setUp() {
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
        @DisplayName("Successfully create goal")
        void whenValidInputThenCreateGoalSuccessfully() {
            when(goalService.createGoal(USER_ID, goalDto)).thenReturn(goalDto);

            GoalDto result = goalController.createGoal(USER_ID, goalDto);

            assertNotNull(result);
            assertEquals(goalDto.getId(), result.getId());
            verify(goalService).createGoal(USER_ID, goalDto);
        }

        @Test
        @DisplayName("Throws DataValidationException on invalid input")
        void whenInvalidInputThenThrowDataValidationException() {
            GoalDto invalidGoal = GoalDto.builder()
                    .title("Title")
                    .description("Description")
                    .build();
            String errorMessage = "Invalid input";

            doThrow(new DataValidationException(errorMessage)).when(goalService).createGoal(USER_ID, invalidGoal);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> goalController.createGoal(USER_ID, invalidGoal));

            assertEquals(errorMessage, exception.getMessage());
            verify(goalService).createGoal(USER_ID, invalidGoal);
        }
    }

    @Nested
    @DisplayName("Update Goal Tests")
    class UpdateGoalTests {

        @Test
        @DisplayName("Successfully update goal")
        void whenValidInputThenUpdateGoalSuccessfully() {
            when(goalService.updateGoal(GOAL_ID, goalDto)).thenReturn(goalDto);

            GoalDto result = goalController.updateGoal(GOAL_ID, goalDto);

            assertNotNull(result);
            assertEquals(goalDto.getId(), result.getId());
            verify(goalService).updateGoal(GOAL_ID, goalDto);
        }

        @Test
        @DisplayName("Throws EntityNotFoundException when goal does not exist")
        void whenGoalDoesNotExistThenThrowEntityNotFoundException() {
            String errorMessage = "Goal not found";

            doThrow(new EntityNotFoundException(errorMessage)).when(goalService).updateGoal(GOAL_ID, goalDto);

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> goalController.updateGoal(GOAL_ID, goalDto));

            assertEquals(errorMessage, exception.getMessage());
            verify(goalService).updateGoal(GOAL_ID, goalDto);
        }
    }

    @Nested
    @DisplayName("Delete Goal Tests")
    class DeleteGoalTests {

        @Test
        @DisplayName("Successfully delete goal")
        void whenGoalExistsThenDeleteSuccessfully() {
            doNothing().when(goalService).deleteGoal(GOAL_ID);

            goalController.deleteGoal(GOAL_ID);

            verify(goalService).deleteGoal(GOAL_ID);
        }

        @Test
        @DisplayName("Throws EntityNotFoundException when goal does not exist")
        void whenGoalDoesNotExistThenThrowEntityNotFoundException() {
            String errorMessage = "Goal not found";

            doThrow(new EntityNotFoundException(errorMessage)).when(goalService).deleteGoal(GOAL_ID);

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> goalController.deleteGoal(GOAL_ID));

            assertEquals(errorMessage, exception.getMessage());
            verify(goalService).deleteGoal(GOAL_ID);
        }
    }

    @Nested
    @DisplayName("Fetch Goals Tests")
    class FetchGoalsTests {

        @Test
        @DisplayName("Fetch subtasks by goal ID")
        void whenGoalIdProvidedThenFetchSubtasks() {
            List<GoalDto> subtasks = List.of(goalDto);

            when(goalService.findSubtasksByGoalId(GOAL_ID, new GoalFilterDto())).thenReturn(subtasks);

            List<GoalDto> result = goalController.findSubtasksByGoalId(GOAL_ID, null, null, null, null);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(goalDto.getId(), result.get(0).getId());
            verify(goalService).findSubtasksByGoalId(GOAL_ID, new GoalFilterDto());
        }

        @Test
        @DisplayName("Fetch goals by user ID")
        void whenUserIdProvidedThenFetchGoals() {
            List<GoalDto> goals = List.of(goalDto);

            when(goalService.getGoalsByUser(USER_ID, new GoalFilterDto())).thenReturn(goals);

            List<GoalDto> result = goalController.getGoalsByUser(USER_ID, null, null, null, null);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(goalDto.getId(), result.get(0).getId());
            verify(goalService).getGoalsByUser(USER_ID, new GoalFilterDto());
        }
    }
}
