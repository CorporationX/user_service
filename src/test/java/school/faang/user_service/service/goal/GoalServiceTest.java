package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exсeption.DataValidationException;
import school.faang.user_service.exсeption.EntityNotFoundException;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private GoalMapperImpl goalMapper;

    @InjectMocks
    private GoalService goalService;

    GoalDto goalDto = mock(GoalDto.class);
    long id = 1;


    @Test
    void updateCompletedGoalTest() {
        Goal old = Goal.builder().status(GoalStatus.COMPLETED).build();
        when(goalRepository.findGoal(id)).thenReturn(old);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.updateGoal(id, goalDto));

        assertEquals("Goal already completed!", exception.getMessage());

        verify(goalRepository).findGoal(id);
    }

    @Test
    void updateGoalSkillFoundTest() {
        Goal old = Goal.builder().status(GoalStatus.ACTIVE).build();
        List<Long> skillIds = List.of(1L, 2L);

        when(goalRepository.findGoal(id)).thenReturn(old);
        when(goalDto.getSkillIds()).thenReturn(skillIds);
        when(skillRepository.countExisting(skillIds)).thenReturn(1);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> goalService.updateGoal(id, goalDto));

        assertEquals("Goal contains non-existent skill!", exception.getMessage());

        verify(goalRepository).findGoal(id);
        verify(goalDto).getSkillIds();
        verify(skillRepository).countExisting(skillIds);
    }

    @Test
    void updateGoalAndCompleteTest() {
        Goal goal = Goal.builder().status(GoalStatus.COMPLETED).build();
        Goal old = Goal.builder().status(GoalStatus.ACTIVE).build();
        List<Long> skillIds = List.of(1L, 2L);

        when(goalRepository.findGoal(id)).thenReturn(old);
        when(goalDto.getSkillIds()).thenReturn(skillIds);
        when(skillRepository.countExisting(skillIds)).thenReturn(2);

        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(goalDto.getSkillIds()).thenReturn(skillIds);
        when(goalRepository.findUsersByGoalId(id)).thenReturn(List.of());

        goalService.updateGoal(id, goalDto);

        verify(goalRepository).deleteById(id);
    }

    @Test
    void updateGoalTest() {
        Goal goal = Goal.builder().status(GoalStatus.ACTIVE).build();
        Goal old = Goal.builder().status(GoalStatus.ACTIVE).build();
        List<Long> skillIds = List.of(1L, 2L);

        when(goalRepository.findGoal(id)).thenReturn(old);
        when(goalDto.getSkillIds()).thenReturn(skillIds);
        when(skillRepository.countExisting(skillIds)).thenReturn(2);

        when(goalMapper.toEntity(goalDto)).thenReturn(goal);

        goalService.updateGoal(id, goalDto);

        verify(goalRepository).save(goal);
    }
}