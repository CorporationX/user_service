package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.goal.SkillService;
import school.faang.user_service.validator.GoalServiceValidate;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillService skillService;
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private GoalServiceValidate validate;

    @Test
    public void testCreateWhenValid() {
        long userId = 1L;
        int count = 2;
        Goal goal = new Goal();
        List<Skill> skills = List.of(new Skill());
        List<String> allGoalTitles = List.of("title");

        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(count);
        when(goalRepository.findAllGoalTitles()).thenReturn(allGoalTitles);

        goalService.createGoal(userId, goal);
        verify(validate, times(1)).validateCreateGoal(userId, goal, count, allGoalTitles);
        verify(goalRepository, times(1)).create(any(String.class), any(String.class), any(Long.class));
        verify(skillService, times(1)).create(skills, userId);
    }

    @Test
    public void testUpdateWhenValid() {
        long goalId = 1L;
        GoalDto goalDto = new GoalDto();
        Goal goal = new Goal();
        goal.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(goal));
        when(goalMapper.toGoal(goalDto)).thenReturn(goal);

        goalService.updateGoal(goalId, goalDto);
        verify(validate, times(1)).validateUpdateGoal(goal, String.valueOf(goal.getStatus()));
        verify(skillService, times(1)).addSkillToUsers(any(List.class), goalId);
    }

    @Test
    public void testDeleteWhenValid() {
        long goalId = 1L;
        Stream<Goal> goal = Stream.of(new Goal());
        when(goalRepository.findByParent(goalId)).thenReturn(goal);

        goalService.deleteGoal(goalId);
        verify(validate, times(1)).validateDeleteGoal(goal);
        verify(goalRepository, times(1)).deleteByGoalId(goalId);
    }
}