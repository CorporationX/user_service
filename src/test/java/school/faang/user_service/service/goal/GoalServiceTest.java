package school.faang.user_service.service.goal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;
    private GoalFilter filter1;
    private GoalFilter filter2;
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private SkillRepository skillRepository;

    @BeforeEach
    void setUp() {
        filter1 = mock(GoalFilter.class);
        filter2 = mock(GoalFilter.class);
        MockitoAnnotations.initMocks(this);
        goalService = new GoalService(goalRepository, goalMapper, new ArrayList<>(),skillRepository);
    }

    @Test
    void testGetGoalsByUser_EmptyGoalFiltersAndEmptyGoalRepository() {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        when(goalRepository.findAll()).thenReturn(new ArrayList<>());

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertEquals(0, result.size());
    }

    @Test
    void testGetGoalsByUser_WithNonApplicableGoalFilter() {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        GoalFilter nonApplicableFilter = mock(GoalFilter.class);
        when(nonApplicableFilter.isApplicable(filters)).thenReturn(false);

        List<GoalFilter> goalFilters = List.of(nonApplicableFilter);
        goalService = new GoalService(goalRepository, goalMapper, goalFilters,skillRepository);

        List<Goal> goals = List.of(new Goal(), new Goal());
        when(goalRepository.findAll()).thenReturn(goals);

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertEquals(0, result.size());
    }

    @Test
    void testGetGoalsByUser_WithApplicableGoalFilter() {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        GoalFilter applicableFilter = mock(GoalFilter.class);
        when(applicableFilter.isApplicable(filters)).thenReturn(true);

        List<GoalFilter> goalFilters = List.of(applicableFilter);
        goalService = new GoalService(goalRepository, goalMapper, goalFilters, skillRepository);

        List<Goal> goals = List.of(new Goal(), new Goal());
        when(goalRepository.findAll()).thenReturn(goals);

        GoalDto goalDto1 = new GoalDto();
        GoalDto goalDto2 = new GoalDto();
        when(goalMapper.toDto(any(Goal.class))).thenReturn(goalDto1, goalDto2);

        Stream<Goal> filteredGoals = goals.stream();
        when(applicableFilter.applyFilter(any(Stream.class), eq(filters))).thenReturn(filteredGoals);

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertEquals(2, result.size());
        assertEquals(goalDto1, result.get(0));
        assertEquals(goalDto2, result.get(1));
        verify(goalMapper, times(2)).toDto(any(Goal.class));
    }

    @Test
    public void testCreateGoal_Success() {
        Long userId = 1L;
        Goal parent = new Goal();
        parent.setId(123L);
        Goal goal = new Goal(1L, parent, "Title", "Description",
                null, null, null, null, null, null, null, null);
        Skill skill = new Skill();
        skill.setTitle("Skill Title");
        goal.setSkillsToAchieve(Collections.singletonList(skill));
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        when(skillRepository.existsByTitle("Skill Title")).thenReturn(true);

        goalService.createGoal(userId, goal);

        verify(goalRepository).create("Title", "Description", parent.getId());
        verify(goalRepository).save(goal);
    }

    @Test
    public void testCreateGoal_WithoutTitle() {
        Long userId = 1L;
        Goal parent = new Goal();
        parent.setId(123L);
        Goal goal = new Goal(1L, parent, null, "Description",
                null, null, null, null, null, null, null, null);

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(userId, goal));

        assertEquals("Goal title cannot be empty", exception.getMessage());
    }

    @Test
    public void testCreateGoal_MaxActiveGoalsExceeded() {
        Long userId = 1L;
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);
        Goal parent = new Goal();
        parent.setId(123L);
        Goal goal = new Goal(1L, parent, "Title", "Description",
                null, null, null, null, null, null, null, null);
        Skill skill = new Skill();
        skill.setTitle("Skill Title");
        goal.setSkillsToAchieve(Collections.singletonList(skill));

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(userId, goal));

        assertEquals("User cannot have more than 3 active goals", exception.getMessage());
    }

    @Test
    public void testCreateGoal_SkillNotExists() {
        Long userId = 1L;

        Goal parent = new Goal();
        parent.setId(123L);
        Goal goal = new Goal(1L, parent, "Title", "Description",
                null, null, null, null, null, null, null, null);
        Skill skill = new Skill();
        skill.setTitle("Nonexistent Skill");
        goal.setSkillsToAchieve(Collections.singletonList(skill));

        Mockito.when(skillRepository.existsByTitle("Nonexistent Skill")).thenReturn(false);

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(userId, goal));

        assertEquals("Skill Nonexistent Skill does not exist", exception.getMessage());
    }

    @Test
    public void testCreateGoal_WithoutSkillsToAchieve() {
        Long userId = 1L;
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(0);

        Goal parent = new Goal();
        parent.setId(123L);
        Goal goal = new Goal(1L, parent, "Title", "Description",
                null, null, null, null, null, null, null, null);
        goal.setSkillsToAchieve(null);

        goalService.createGoal(userId, goal);

        Mockito.verify(goalRepository).create("Title", "Description", parent.getId());

        Mockito.verifyNoMoreInteractions(goalRepository);
    }
}