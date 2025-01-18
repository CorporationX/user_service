package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private GoalFilter goalFilter;

    @Mock
    private List<GoalFilter> goalFilters;

    @InjectMocks
    private GoalService goalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(goalService, "maxActiveGoalsPerUser", 3);
    }

    @Test
    void createGoal_ShouldReturnCreatedGoal() {
        Goal goal = new Goal();
        goal.setId(1L);
        when(goalRepository.create(anyString(), anyString(), anyLong())).thenReturn(goal);
        when(skillService.findSkillById(anyLong())).thenReturn(Optional.of(new Skill()));

        Goal result = goalService.createGoal(1L, "title", "description", 1L, List.of(1L));

        assertEquals(goal, result);
        verify(goalRepository, times(1)).countActiveGoalsPerUser(1L);
        verify(skillService, times(1)).findSkillById(1L);
        verify(goalRepository, times(1)).create("title", "description", 1L);
        verify(goalRepository, times(1)).addSkillToGoalById(1L, 1L);
    }

    @Test
    void createGoal_ShouldThrowExceptionWhenExceedsMaxActiveGoals() {
        when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(3);

        assertThrows(IllegalStateException.class, () -> goalService.createGoal(1L, "title", "description", 1L, List.of(1L)));
    }

    @Test
    void createGoal_ShouldThrowException_NotExistentSkill() {
        when(skillService.findSkillById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> goalService.createGoal(1L, "title", "description", 1L, List.of(1L)));
    }

    @Test
    void updateGoal_ShouldReturnUpdatedGoal() {
        Goal goal = new Goal();
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(new Goal()));
        when(skillService.findSkillById(anyLong())).thenReturn(Optional.of(new Skill()));

        Goal result = goalService.updateGoal(1L, goal, List.of(1L));

        assertEquals(goal, result);
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(goal);
        verify(goalRepository, times(1)).removeSkillsFromGoal(1L);
        verify(goalRepository, times(1)).addSkillToGoalById(1L, 1L);
    }

    @Test
    void updateGoalToCompleted_ShouldReturnUpdatedGoal() {
        Goal goal = new Goal();
        goal.setStatus(COMPLETED);
        User user = new User();
        goal.setUsers(List.of(user));
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(new Goal()));
        when(skillService.findSkillById(anyLong())).thenReturn(Optional.of(new Skill()));

        Goal result = goalService.updateGoal(1L, goal, List.of(1L));

        assertEquals(goal, result);
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(goal);
        verify(goalRepository, times(1)).removeSkillsFromGoal(1L);
        verify(goalRepository, times(1)).addSkillToGoalById(1L, 1L);
        verify(skillService, times(1)).assignSkillsFromGoalToUsers(1L, goal.getUsers());
    }

    @Test
    void updateGoal_ShouldThrowNoSuchElementException_WhenGoalNotFound() {
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> goalService.updateGoal(1L, new Goal(), List.of()));
    }

    @Test
    void updateGoal_ShouldThrowIllegalStateException_WhenGoalIsCompleted() {
        Goal goal = new Goal();
        goal.setStatus(COMPLETED);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        assertThrows(IllegalStateException.class, () -> goalService.updateGoal(1L, goal, List.of()));
    }

    @Test
    void updateGoal_ShouldThrowNoSuchElementException_WhenSkillNotFound() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(new Goal()));
        when(skillService.findSkillById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> goalService.updateGoal(1L, new Goal(), List.of(1L)));
    }

    @Test
    void deleteGoal_ShouldInvokeRepositoryDelete() {
        doNothing().when(goalRepository).deleteById(anyLong());

        goalService.deleteGoal(1L);

        verify(goalRepository, times(1)).deleteById(1L);
    }

    @Test
    void findSubGoalsByParentId_ShouldReturnSubGoals() {
        Goal goal = new Goal();
        when(goalRepository.findByParent(1L)).thenReturn(Stream.of(goal));
        when(goalFilter.isApplicable(any())).thenReturn(true);
        when(goalFilter.apply(any(GoalFilterDto.class), any(Goal.class))).thenReturn(true);
        when(goalFilters.stream()).thenReturn(Stream.of(goalFilter));

        List<Goal> result = goalService.findSubGoalsByParentId(1L, new GoalFilterDto());

        assertEquals(List.of(goal), result);
        verify(goalRepository, times(1)).findByParent(1L);
    }

    @Test
    void findSubGoalsByParentId_FilterNotApply() {
        Goal goal = new Goal();
        when(goalRepository.findByParent(1L)).thenReturn(Stream.of(goal));
        when(goalFilter.isApplicable(any())).thenReturn(true);
        when(goalFilter.apply(any(GoalFilterDto.class), any(Goal.class))).thenReturn(false);
        when(goalFilters.stream()).thenReturn(Stream.of(goalFilter));

        List<Goal> result = goalService.findSubGoalsByParentId(1L, new GoalFilterDto());

        assertTrue(result.isEmpty());
        verify(goalRepository, times(1)).findByParent(1L);
    }

    @Test
    void findSubGoalsByParentId_NoApplicableFilters() {
        Goal goal = new Goal();
        when(goalRepository.findByParent(1L)).thenReturn(Stream.of(goal));
        when(goalFilter.isApplicable(any())).thenReturn(false);
        when(goalFilter.apply(any(GoalFilterDto.class), any(Goal.class))).thenReturn(true);
        when(goalFilters.stream()).thenReturn(Stream.of(goalFilter));

        List<Goal> result = goalService.findSubGoalsByParentId(1L, new GoalFilterDto());

        assertEquals(List.of(goal), result);
        verify(goalRepository, times(1)).findByParent(1L);
    }

    @Test
    void findSubGoalsByUserId_ShouldReturnSubGoals() {
        Goal goal = new Goal();
        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal));
        when(goalFilter.isApplicable(any())).thenReturn(true);
        when(goalFilter.apply(any(GoalFilterDto.class), any(Goal.class))).thenReturn(true);
        when(goalFilters.stream()).thenReturn(Stream.of(goalFilter));

        List<Goal> result = goalService.findSubGoalsByUserId(1L, new GoalFilterDto());

        assertEquals(List.of(goal), result);
        verify(goalRepository, times(1)).findGoalsByUserId(1L);
    }

    @Test
    void findSubGoalsByUserId_FilterNotApply() {
        Goal goal = new Goal();
        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal));
        when(goalFilter.isApplicable(any())).thenReturn(true);
        when(goalFilter.apply(any(GoalFilterDto.class), any(Goal.class))).thenReturn(false);
        when(goalFilters.stream()).thenReturn(Stream.of(goalFilter));

        List<Goal> result = goalService.findSubGoalsByUserId(1L, new GoalFilterDto());

        assertTrue(result.isEmpty());
        verify(goalRepository, times(1)).findGoalsByUserId(1L);
    }

    @Test
    void findSubGoalsByUserId_NoApplicableFilters() {
        Goal goal = new Goal();
        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal));
        when(goalFilter.isApplicable(any())).thenReturn(false);
        when(goalFilter.apply(any(GoalFilterDto.class), any(Goal.class))).thenReturn(true);
        when(goalFilters.stream()).thenReturn(Stream.of(goalFilter));

        List<Goal> result = goalService.findSubGoalsByUserId(1L, new GoalFilterDto());

        assertEquals(List.of(goal), result);
        verify(goalRepository, times(1)).findGoalsByUserId(1L);
    }
}