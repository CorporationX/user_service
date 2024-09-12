package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {

    @InjectMocks
    private GoalController goalController;

    @Mock
    private GoalService goalService;

    private long goalId;
    private GoalDto goalDto;
    private long userId;

    @BeforeEach
    public void setUp() {
        goalId = 1L;
        userId = 1L;
        goalDto = GoalDto.builder()
                .id(1L)
                .description("test")
                .title("test")
                .status(GoalStatus.ACTIVE)
                .build();
    }

    @DisplayName("Когда goal был создан")
    @Test
    public void testCreateWhenValid() {
        goalController.createGoal(userId, goalDto);
        verify(goalService, times(1)).createGoal(userId, goalDto);
    }

    @DisplayName("Когда метод обновления goal отработал")
    @Test
    public void testUpdateGoalWhenValid() {
        goalController.updateGoal(goalId, goalDto);
        verify(goalService, times(1)).updateGoal(goalId, goalDto);
    }

    @DisplayName("Когда goal был успешно удален")
    @Test
    public void testDeleteGoalWhenValid() {
        goalController.deleteGoal(goalId);
        verify(goalService, times(1)).deleteGoal(goalId);
    }

    @DisplayName("Успешное получение всх подзадачи цели")
    @Test
    public void testGetSubtasksByGoalIdWhenValid() {
        GoalFilterDto filters = new GoalFilterDto();

        goalController.getSubtasksByGoalId(goalId, filters);
        verify(goalService, times(1)).getSubtasksByGoalId(goalId, filters);
    }

    @DisplayName("Успешное получение целей по фильтру")
    @Test
    public void testGetGoalsByUserWhenValid() {
        GoalFilterDto filters = new GoalFilterDto();

        goalController.getGoalsByUser(userId, filters);
        verify(goalService, times(1)).getGoalsByUser(userId, filters);
    }
}