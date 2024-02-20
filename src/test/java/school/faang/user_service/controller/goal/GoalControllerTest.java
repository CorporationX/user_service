package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {
    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    GoalFilterDto goalFilterDto;
    List<GoalDto> goalsDto;

    @BeforeEach
    void init() {
        goalFilterDto = GoalFilterDto.builder()
                .title("Java")
                .goalStatus(GoalStatus.ACTIVE)
                .build();

        goalsDto = List.of(new GoalDto());
    }


    @Test
    public void testSuccessfullyDeleteGoal() {
        goalController.deleteGoal(1L);

        verify(goalService, times(1)).deleteGoal(1L);
    }

    @Test
    public void testDeleteGoalThrowsDataValidationException() {
        assertThrows(DataValidationException.class, () -> goalController.deleteGoal(null));
    }

    @Test
    public void testSuccessfullyGetGoalsByUser() {
        when(goalController.getGoalsByUser(1L, goalFilterDto)).thenReturn(goalsDto);
        goalController.getGoalsByUser(1L, goalFilterDto);

        verify(goalService, times(1)).getGoalsByUser(1L, goalFilterDto);
    }

    @Test
    public void testGoalsByUserThrowsDataValidationException() {
        assertThrows(DataValidationException.class,
                () -> goalController.getGoalsByUser(null, goalFilterDto));
    }

    @Test
    public void testSuccessfullyFindSubtasksByGoalId() {
        when(goalController.findSubtasksByGoalId(1L)).thenReturn(goalsDto);
        goalController.findSubtasksByGoalId(1L);

        verify(goalService, times(1)).findSubtasksByGoalId(1L);
    }

    @Test
    public void testFindSubtasksByGoalIdThrowsDataValidationException() {
        assertThrows(DataValidationException.class,
                () -> goalController.retrieveFilteredSubtasksForGoal(null, goalFilterDto));
    }

    @Test
    public void testSuccessfullyFindSubtasksByGoalIdAndFilters() {
        when(goalController.retrieveFilteredSubtasksForGoal(1L, goalFilterDto)).thenReturn(goalsDto);
        goalController.retrieveFilteredSubtasksForGoal(1L, goalFilterDto);

        verify(goalService, times(1)).retrieveFilteredSubtasksForGoal(1L, goalFilterDto);
    }

    @Test
    public void testFindSubtasksByGoalIdAndFiltersThrowsDataValidationException() {
        assertThrows(DataValidationException.class,
                () -> goalController.retrieveFilteredSubtasksForGoal(null, goalFilterDto));
    }

    @Test
    public void testUpdateByGoalIdAndFiltersThrowsDataValidationException() {
        GoalDto goalDto = new GoalDto();
        assertThrows(DataValidationException.class,
                () -> goalController.updateGoal(1L, goalDto));
    }
}