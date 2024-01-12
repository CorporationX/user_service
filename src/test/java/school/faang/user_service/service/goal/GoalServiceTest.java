package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;
    Goal goal;

    @BeforeEach
    void init() {
        goal = new Goal();
        goal.setId(1L);
    }

    @Test
    @DisplayName("Check for search by id")
    void testDeleteGoal() {
        // Mock для поиска  по id
        Mockito.when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        //после успешного поиска, вызываем метод для удаления
        goalService.deleteGoal(1L);
        //проверяем вызвался ли метод
        Mockito.verify(goalRepository, Mockito.times(1)).delete(goal);
    }

    @Test
    @DisplayName("Missing target remove test")
    void testGoalNotFound() {
        //Mock для не отсутствующего id
        Mockito.when(goalRepository.findById(1L)).thenReturn(Optional.empty());
        //проверяем кидает ли исключение
        assertThrows(IllegalArgumentException.class, () -> goalService.deleteGoal(1L));
    }
}