package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.ResponseGoalDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private GoalMapper goalMapper = GoalMapper.INSTANCE;
    @InjectMocks
    private GoalService goalService;

    @Test
    void createGoal_With_Null_Goal() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(1L, null));

        assertEquals("Goal cannot be null", exception.getMessage());
    }

    @Test
    void createGoal_With_Blank_Title() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(1L, CreateGoalDto.builder().title("").build()));

        assertEquals("Title cannot be null", exception.getMessage());
    }

    @Test
    void createGoal_When_Max_User_Reached() {
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(3);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(1L, CreateGoalDto.builder().title("title").build()));

        assertEquals("Maximum number of goals for this user reached", exception.getMessage());
    }

    @Test
    void createGoal_When_Skill_Not_Exist() {
        CreateGoalDto goal = CreateGoalDto.builder()
                .title("title")
                .skillsToAchieve(List.of(
                        mock(SkillDto.class),
                        mock(SkillDto.class),
                        mock(SkillDto.class)
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

    @Test
    void createGoal_Successful() {
        CreateGoalDto goalDto = CreateGoalDto.builder()
                .title("title")
                .skillsToAchieve(List.of(
                        mock(SkillDto.class),
                        mock(SkillDto.class),
                        mock(SkillDto.class)
                ))
                .build();

        Goal goal = goalMapper.toGoalFromCreateGoalDto(goalDto);

        when(skillRepository.existsByTitle(any()))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true);
        when(goalRepository.save(any(Goal.class))).thenAnswer(i -> {
            goal.setId(2L);
            return goal;
        });

        ResponseGoalDto actual = goalService.createGoal(1L, goalDto);
        ResponseGoalDto expected = goalMapper.toResponseGoalDtoFromGoal(goal);
        expected.setId(2L);

        assertEquals(expected, actual);
    }
}
