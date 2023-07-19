package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private GoalService goalService;

    @Test
    void MaxActiveGoalsTest() {
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(3);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(1L, Goal.builder().title("title").build()));

        assertEquals("Out of MAX_ACTIVE_GOALS range", exception.getMessage());
    }

    @Test
    void createGoalSkillNotExist() {
        Goal goal = Goal.builder().title("title").skillsToAchieve(List.of(mock(Skill.class), mock(Skill.class))).build();

        when(skillRepository.existsByTitle(any()))
                .thenReturn(true)
                .thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(1L, goal));

        assertEquals("Contains a non-existence skill", exception.getMessage());

        verify(skillRepository, times(2)).existsByTitle(any());
    }
}