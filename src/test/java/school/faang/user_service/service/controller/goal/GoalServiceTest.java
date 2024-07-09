package school.faang.user_service.service.controller.goal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.filter.GoalFilters;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.goal.SkillService;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GoalServiceTest {

    @InjectMocks
    GoalService goalService;

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillService skillService;
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private List<GoalFilters> goalFilters;

    @Test
    public void testCreateMaxNumbersGoalUser() {
        long userId = 1L;
        Goal goal = new Goal();

        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);
        assertThrows(IllegalStateException.class, () -> goalService.createGoal(userId, goal));
    }

    @Test
    public void testCreateExistsByTitleSkill() {
        long userId = 1L;
        Goal goal = new Goal();

        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(1);
        assertThrows(IllegalStateException.class, () -> goalService.createGoal(userId, goal));
    }

    @Test
    public void testCreateWhenValid() {
        long userId = 1L;
        Goal goal = new Goal();
        goal.setTitle("New Goal");
        List<Skill> skills = Arrays.asList(new Skill());
        goal.setSkillsToAchieve(skills);

        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        when(skillService.existsByTitle(skills)).thenReturn(true);

        goalService.createGoal(userId, goal);
        verify(goalRepository, times(1)).create(any(String.class), any(String.class), any(Long.class));
        verify(skillService, times(1)).create(skills, userId);
    }

    @Test
    public void testUpdateGoalStatusCompleted() {
        Long goalId = 1L;
        GoalDto goalDto = new GoalDto();
        Goal goal = new Goal();
        goal.setStatus(GoalStatus.COMPLETED);

        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(goal));

        assertThrows(IllegalStateException.class, () -> goalService.updateGoal(goalId, goalDto));
    }

    @Test
    public void testUpdateWhenValid() {
        Long goalId = 1L;
        GoalDto goalDto = new GoalDto();
        Goal goal = new Goal();
        goal.setStatus(GoalStatus.ACTIVE);
        List<Skill> skills = Arrays.asList(new Skill());
        goal.setSkillsToAchieve(skills);

        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(goal));
        when(goalMapper.toGoal(goalDto)).thenReturn(goal);
        when(skillService.existsByTitle(skills)).thenReturn(true);

        goalService.updateGoal(goalId, goalDto);
        verify(skillService, times(1)).addSkillToUsers(any(List.class), goalId);
    }

    @Test
    public void testDeleteGoalIsEmpty() {
        Long goalId = 1L;
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.empty());

        assertThrows(NoSuchElementException.class, () -> goalService.deleteGoal(goalId));
    }

    @Test
    public void testDeleteWhenValid() {
        Long goalId = 1L;
        Goal goal = new Goal();
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(goal));

        goalService.deleteGoal(goalId);
        verify(goalRepository, times(1)).deleteByGoalId(goalId);
    }
}