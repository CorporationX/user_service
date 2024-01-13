package school.faang.user_service.controller.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;
    Goal goal;

    @BeforeEach
    void init() {
        goal = Goal.builder()
                .id(1L)
                .build();
    }

    @Test
    @DisplayName("test Controller")
    void testDeleteGoal() {
        // вызываем метод
        goalService.deleteGoal(1L);
        // проверяем сработал ли
        Mockito.verify(goalService).deleteGoal(1L);

    }

    @Test
    @DisplayName("test Exception")
    void testGoalIdNullOrNegativeMeaning() {
        assertThrows(IllegalArgumentException.class, () -> goalController.deleteGoal(-2L));
    }
}