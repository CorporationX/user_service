package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.GoalService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    @Mock
    private GoalService goalService;
    @InjectMocks
    private GoalController goalController;

    @Test
    void testCreateWhenReceivedGoalWithBlankTitle() {
        Goal goal = init("");
        assertThrows(DataValidationException.class, () -> goalController.createGoal(1L, goal));
    }

    @Test
    void testCreateWhenAllCorrect() {
        Goal goal = init("Test");

        goalController.createGoal(1L, goal);

        Mockito.verify(goalService, Mockito.times(1)).createGoal(1L, goal);
    }

    @Test
    void testDelete() {
        goalController.deleteGoal(1L);
        Mockito.verify(goalService, Mockito.times(1)).deleteGoal(1L);
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

        Mockito.verify(goalService, Mockito.times(1)).updateGoal(1L, goalDto);
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