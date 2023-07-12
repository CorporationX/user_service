package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void getGoalsByUserTest() {
        Stream<Goal> goals = Stream.of(
                mock(Goal.class),
                mock(Goal.class)
        );
        GoalFilterDto filterDto = new GoalFilterDto();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(goals);

        List<GoalDto> goalDtos = goalService.getGoalsByUser(1L, filterDto);

        assertNotNull(goalDtos);
        assertEquals(2, goalDtos.size());

        verify(goalRepository).findGoalsByUserId(1L);
    }

    @Test
    void createGoal_With_Null_Goal() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(1L, null));

        assertEquals("Goal cannot be null", exception.getMessage());
    }

    @Test
    void createGoal_With_Blank_Title() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(1L, Goal.builder().title("").build()));

        assertEquals("Title cannot be null", exception.getMessage());
    }

    @Test
    void createGoal_When_Max_User_Reached() {
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(3);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(1L, Goal.builder().title("title").build()));

        assertEquals("Maximum number of goals for this user reached", exception.getMessage());
    }

    @Test
    void createGoal_When_Skill_Not_Exist() {
        Goal goal = Goal.builder()
                .title("title")
                .skillsToAchieve(List.of(
                        mock(Skill.class),
                        mock(Skill.class),
                        mock(Skill.class)
                ))
                .build();

        when(skillRepository.existsByTitle(any()))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(1L, goal));

        assertEquals("Skill not found", exception.getMessage());

        verify(skillRepository, times(3)).existsByTitle(any());
    }
}
