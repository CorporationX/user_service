package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private GoalService goalService;

    Goal goal = mock(Goal.class);
    Skill skill = mock(Skill.class);
    User user = mock(User.class);

    @BeforeEach
    void setUp() {
        when(goal.getSkillsToAchieve()).thenReturn(List.of(skill));
        when(goal.getUsers()).thenReturn(List.of(user));
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(goal));
    }

    @Test
    void updateCompletedGoalTest() {
        when(goal.getTitle()).thenReturn("title");
        when(goal.getStatus()).thenReturn(GoalStatus.COMPLETED);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.updateGoal(0L, goal));

        assertEquals("Goal already completed", exception.getMessage());

        verify(goalRepository).findGoal(anyLong());
    }

    @Test
    void updateGoalSkillFoundTest() {
        when(goal.getTitle()).thenReturn("Java");
        when(goal.getStatus()).thenReturn(GoalStatus.ACTIVE);
        when(skillRepository.existsByTitle(anyString())).thenReturn(false);
        when(skill.getTitle()).thenReturn("Spring");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.updateGoal(0L, goal));

        assertEquals("Goal contains non-existent skill", exception.getMessage());
    }

    @Test
    void updateGoalAndCompleteTest() {
        when(goal.getTitle()).thenReturn("Java");
        when(goal.getStatus()).thenReturn(GoalStatus.ACTIVE);
        when(skillRepository.existsByTitle(anyString())).thenReturn(true);
        when(skill.getTitle()).thenReturn("Spring");

        goal.setStatus(GoalStatus.COMPLETED);
        goalService.updateGoal(0L, goal);

        verify(goalRepository).delete(goal);
    }

    @Test
    void updateGoalTest() {
        when(goal.getTitle()).thenReturn("Java");
        when(goal.getStatus()).thenReturn(GoalStatus.ACTIVE);
        when(skillRepository.existsByTitle(anyString())).thenReturn(true);
        when(skill.getTitle()).thenReturn("Spring");

        goalService.updateGoal(0L, goal);

        verify(goalRepository).save(goal);
    }
}