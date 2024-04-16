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
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

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
    void testSuccessfullyDeleteGoal() {
        goalController.deleteGoal(1L);

        verify(goalService, times(1)).deleteGoal(1L);
    }

    @Test
    void testSuccessfullyGetGoalsByUser() {
        when(goalController.getGoalsByUser(1L, goalFilterDto)).thenReturn(goalsDto);
        goalController.getGoalsByUser(1L, goalFilterDto);

        verify(goalService, times(1)).getGoalsByUser(1L, goalFilterDto);
    }

    @Test
    void testSuccessfullyFindSubtasksByGoalId() {
        when(goalController.findSubtasksByGoalId(1L)).thenReturn(goalsDto);
        goalController.findSubtasksByGoalId(1L);

        verify(goalService, times(1)).findSubtasksByGoalId(1L);
    }

    @Test
    void testSuccessfullyFindSubtasksByGoalIdAndFilters() {
        when(goalController.retrieveFilteredSubtasksForGoal(1L, goalFilterDto)).thenReturn(goalsDto);
        goalController.retrieveFilteredSubtasksForGoal(1L, goalFilterDto);

        verify(goalService, times(1)).retrieveFilteredSubtasksForGoal(1L, goalFilterDto);
    }

}