package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private GoalValidator validator;
    @Spy
    private GoalMapperImpl goalMapper;

    @InjectMocks
    private GoalService goalService;

    GoalDto goalDto = mock(GoalDto.class);
    long id = 1;

    @Test
    void updateGoalAndCompleteTest() {
        Goal goal = Goal.builder().status(GoalStatus.COMPLETED).build();
        List<Long> skillIds = List.of(1L, 2L);

        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(goalDto.getSkillIds()).thenReturn(skillIds);
        when(goalRepository.findUsersByGoalId(id)).thenReturn(List.of(User.builder().build()));

        goalService.updateGoal(id, goalDto);

        verify(skillRepository, times(2)).assignSkillToUser(anyLong(), anyLong());
    }

    @Test
    void updateGoalTest() {
        Goal goal = Goal.builder().status(GoalStatus.ACTIVE).build();
        List<Long> skillIds = List.of(1L, 2L);

        when(goalMapper.toEntity(goalDto)).thenReturn(goal);

        goalService.updateGoal(id, goalDto);

        verify(goalRepository).save(goal);
    }
}