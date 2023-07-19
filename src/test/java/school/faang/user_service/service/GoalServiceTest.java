package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private GoalMapper goalMapper = GoalMapper.INSTANCE;
    @InjectMocks
    private GoalService goalService;
    private final UpdateGoalDto goalDto = new UpdateGoalDto();
    private final Goal goal = new Goal();

    @BeforeEach
    public void setUp() {
        goalDto.setId(1L);
        goalDto.setSkillDtos(List.of(SkillDto.builder().title("skillTitle").build()));
        goalDto.setUserIds(List.of(2L));
    }

    @Test
    void updateGoal_With_Blank_Title_Throw_Exception() {
        goalDto.setTitle("");
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.updateGoal(goalDto));

        assertEquals("Title cannot be blank", exception.getMessage());

        verify(goalRepository).findById(anyLong());
    }

    @Test
    void updateGoal_Completed_Goal_Throw_Exception() {
        goalDto.setTitle("Title");
        goal.setStatus(GoalStatus.COMPLETED);
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.updateGoal(goalDto));

        assertEquals("Goal already completed", exception.getMessage());

        verify(goalRepository).findById(anyLong());
    }

    @Test
    void updateGoal_Skill_Not_Found_Throw_Exception() {
        goalDto.setTitle("Title");
        goal.setStatus(GoalStatus.ACTIVE);
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.updateGoal(goalDto));

        assertEquals("Skill skillTitle not found", exception.getMessage());

        verify(goalRepository).findById(anyLong());
        verify(skillRepository).existsByTitle(anyString());
    }
}