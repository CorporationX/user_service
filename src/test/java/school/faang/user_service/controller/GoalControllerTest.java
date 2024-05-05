package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.filter.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    @Mock
    private GoalService goalService;
    @InjectMocks
    private GoalController goalController;

    @Test
    void testCreateWhenGoalIsNull() {
        Goal goal = null;
        assertThrows(NullPointerException.class, () -> goalController.createGoal(1L, goal));
    }

    @Test
    void testCreateWhenReceivedGoalWithBlankTitle() {
        Goal goal = init("");
        assertThrows(DataValidationException.class, () -> goalController.createGoal(1L, goal));
    }

    @Test
    void testCreateWhenAllCorrect() {
        Goal goal = init("Test");

        goalController.createGoal(1L, goal);

        Mockito.verify(goalService).createGoal(1L, goal);
    }

    @Test
    void testDelete() {
        goalController.deleteGoal(1L);
        Mockito.verify(goalService).deleteGoal(1L);
    }

    @Test
    void testUpdateWhenGoalDtoIsNull() {
        GoalDto goalDto = null;

        assertThrows(NullPointerException.class, () -> goalController.updateGoal(1L, goalDto));
    }

    @Test
    void testUpdateWhereGoalDtoHaveBlankTitle() {
        GoalDto goalDto = initDto("");

        assertThrows(DataValidationException.class, () -> goalController.updateGoal(1L, goalDto));
    }

    @Test
    void testUpdateWithCorrectData() {
        GoalDto goalDto = initDto("Test");

        goalController.updateGoal(1L, goalDto);

        Mockito.verify(goalService).updateGoal(1L, goalDto);
    }

    @Test
    void testFindSubtaskByGoalId() {
        Mockito.when(goalService.findSubtasksByGoalId(1L, new GoalFilterDto()))
                .thenReturn(List.of(new GoalDto(), new GoalDto()));
        assertEquals(2, goalController.findSubtasksByGoalId(1L, new GoalFilterDto()).size());
    }

    @Test
    void testGetGoalsByUser() {
        Mockito.when(goalService.getGoalsByUser(1L, new GoalFilterDto()))
                .thenReturn(List.of(new GoalDto(), new GoalDto()));
        assertEquals(2, goalController.getGoalsByUser(1L, new GoalFilterDto()).size());
    }

    private Goal init(String title) {
        Goal goal = new Goal();
        goal.setTitle(title);
        return goal;
    }

    private GoalDto initDto(String title) {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(title);
        return goalDto;
    }
}