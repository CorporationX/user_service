package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private GoalMapperImpl goalMapper;
    @Mock
    private GoalValidator validator;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private GoalService service;

    long userId = 1;
    GoalDto goalDto = new GoalDto();

    @Test
    void deleteGoalValidationTest() {
        when(goalRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.deleteGoal(anyLong()));

        assertEquals("Goal does not exist", exception.getMessage());

        verify(goalRepository, times(0)).deleteById(anyLong());
    }

    @Test
    public void findGoalsByUserIdLessThanOneTest() {
        assertThrows(DataValidationException.class, () -> {
            service.findGoalsByUserId(0L);
        });
    }

    @Test
    void updateGoalAndCompleteTest() {
        goalDto.setStatus(GoalStatus.COMPLETED);
        Goal goal = Goal.builder().status(GoalStatus.COMPLETED).build();
        List<Long> skillIds = List.of(1L, 2L);
        goalDto.setSkillIds(skillIds);

        when(goalRepository.findById(1L)).thenReturn(Optional.ofNullable(Goal.builder().build()));
        when(goalRepository.findUsersByGoalId(1L)).thenReturn(List.of(User.builder().build()));

        service.updateGoal(1L, goalDto);

        verify(skillRepository, times(2)).assignSkillToUser(anyLong(), anyLong());
    }

    @Test
    void deleteGoalTest(){
        when(goalRepository.existsById(1L)).thenReturn(true);

        service.deleteGoal(1L);

        verify(goalRepository).deleteById(1L);
    }

    @Test
    void updateGoalTest() {
        goalDto.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.findById(1L)).thenReturn(Optional.ofNullable(Goal.builder().build()));

        service.updateGoal(1L, goalDto);

        verify(goalRepository).save(goalMapper.toEntity(goalDto));
    }

    @Test
    public void getGoalsByUserLessThanOneTest() {
        assertThrows(DataValidationException.class, () -> {
            service.getGoalsByUser(0L, null);
        });
    }


    @Test
    void createGoalTest() {
        service.createGoal(goalDto);
        verify(goalRepository).save(goalMapper.toEntity(goalDto));

    }
}