package school.faang.user_service.service.goal;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    private long userId;

    @Test
        //Проверить, что вызывается метод delete у репозитория (если объект найден)
    void testShouldDelete() {
        userId = 1;
        Mockito.when(goalRepository.existsById(userId)).thenReturn(true);
        goalService.deleteGoal(userId);
        Mockito.verify(goalRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    void deleteUncorrectedGoalIdTest(){
        userId = 1;
        Mockito.when(goalRepository.existsById(userId)).thenReturn(false);
        DataValidationException dataValidationException = Assert.assertThrows(DataValidationException.class,
                () -> goalService.deleteGoal(userId));
        Assertions.assertEquals("Такой цели нет", dataValidationException.getMessage());
    }
}