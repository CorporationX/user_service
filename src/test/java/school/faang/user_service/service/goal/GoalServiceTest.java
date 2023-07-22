package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.exeptions.EntityNotFoundException;
import school.faang.user_service.repository.goal.GoalRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GoalServiceTest {

    @Mock
    GoalRepository goalRepository;

    @ExtendWith(MockitoExtension.class)
    @InjectMocks
    GoalService service;

    @Test
    void deleteGoalValidationTest() {
        when(goalRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.deleteGoal(anyLong()));

        assertEquals("Goal does not exist", exception.getMessage());

        verify(goalRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void deleteGoalTest(){
        when(goalRepository.existsById(1L)).thenReturn(true);

        service.deleteGoal(1L);

        verify(goalRepository).deleteById(1L);
    }
}