package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.goal.GoalRepository;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    private long userId;

    @BeforeEach
    public void init() {
        userId = 1;
    }

    @Test
        //Проверить, что вызывается метод delete у репозитория (если объект найден)
    void testShouldDelete() {
        goalService.deleteGoal(userId);
        Mockito.verify(goalRepository, Mockito.times(1)).deleteById(userId);
    }
}