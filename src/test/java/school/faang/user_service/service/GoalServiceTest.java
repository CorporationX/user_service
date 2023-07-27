package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private GoalService goalService;

//    @Test
    void deleteGoalTest_Fail() {
        when(goalRepository.existsById(anyLong())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.deleteGoal(anyLong()));

        assertEquals("Goal is not found", exception.getMessage());

        verify(goalRepository, times(0)).deleteById(anyLong());
    }

//    @Test
    void deleteGoalTest_Success(){
        when(goalRepository.existsById(1L)).thenReturn(true);

        goalService.deleteGoal(1L);

        verify(goalRepository).deleteById(1L);
    }
}
