package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.GoalSkillFilter;
import school.faang.user_service.filter.goal.GoalStatusFilter;
import school.faang.user_service.filter.goal.GoalTitleFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
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


    @BeforeEach
    public void setUp() {
        List<GoalFilter> goalFilters = List.of(new GoalTitleFilter(),
                new GoalSkillFilter(), new GoalStatusFilter());

        goalService = new GoalService(goalRepository, skillRepository, goalMapper, goalFilters);
    }

    @Test
    void deleteGoalTest_Fail() {
        when(goalRepository.existsById(anyLong())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goalService.deleteGoal(anyLong()));

        assertEquals("Goal is not found", exception.getMessage());

        verify(goalRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void deleteGoalTest_Success() {
        when(goalRepository.existsById(1L)).thenReturn(true);

        goalService.deleteGoal(1L);

        verify(goalRepository).deleteById(1L);
    }

    @Test
    void getGoalsByUserTest() {
        List<Skill> rightSkill = List.of(Skill.builder().id(1L).build());
        List<Skill> wrongSkill = List.of(Skill.builder().id(2L).build());
        Stream<Goal> goals = Stream.of(
                Goal.builder().id(1L).title("Wrong").status(GoalStatus.ACTIVE).build(),
                Goal.builder().id(2L).title("Title").status(GoalStatus.COMPLETED).build(),
                Goal.builder().id(3L).title("Title").status(GoalStatus.ACTIVE).build(),
                Goal.builder().id(4L).title("Title").status(GoalStatus.ACTIVE).build()
        );
        GoalFilterDto filterDto = new GoalFilterDto("Title", GoalStatus.ACTIVE, 1L);

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(goals);
        when(skillRepository.findSkillsByGoalId(1L)).thenReturn(rightSkill);
        when(skillRepository.findSkillsByGoalId(2L)).thenReturn(rightSkill);
        when(skillRepository.findSkillsByGoalId(3L)).thenReturn(wrongSkill);
        when(skillRepository.findSkillsByGoalId(4L)).thenReturn(rightSkill);

        List<GoalDto> goalDtos = goalService.getGoalsByUser(1L, filterDto);

        assertNotNull(goalDtos);
        assertEquals(1, goalDtos.size());

        verify(goalRepository).findGoalsByUserId(1L);
    }
}
