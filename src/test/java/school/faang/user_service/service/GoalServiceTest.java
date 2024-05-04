package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.UpdatingCompletedGoalException;
import school.faang.user_service.exceptions.UserGoalsValidationException;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Test
    void testCreateWhenUserHaveActiveGoalsGrandThenMaxValue() {
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(3);

        assertThrows(UserGoalsValidationException.class, () -> goalService.createGoal(1L, new Goal()));
    }

    @Test
    void testCreateWhenSomeSkillsDoesntExistInDB() {
        Goal goal = init(false);

        assertThrows(DataValidationException.class, () -> goalService.createGoal(1L, goal));
    }

    @Test
    void testCreateWhenAllCorrect() {
        Goal goal = init(true);

        goalService.createGoal(1L, goal);

        verify(goalRepository, times(1)).create(goal.getTitle(), goal.getDescription(), goal.getId());
    }

    @Test
    void testDelete() {
        goalService.deleteGoal(1L);
        verify(goalRepository, times(1)).deleteGoalById(1L);
    }

    @Test
    void testUpdateWhereGoalStatusIsCompleted() {
        GoalDto goalDto = updateInit(GoalStatus.COMPLETED);

        assertThrows(UpdatingCompletedGoalException.class, () -> goalService.updateGoal(1L, goalDto));
    }

    @Test
    void testUpdateWhereGoalContainsNonExistingSkills() {
        GoalDto goalDto = updateInit(GoalStatus.ACTIVE);
        when(skillService.checkAmountSkillsInDB(goalDto.getSkillIds())).thenReturn(2);

        assertThrows(DataValidationException.class, () -> goalService.updateGoal(1L, goalDto));
    }

    @Test
    void testUpdateWhereGoalStatusUpdatingToCompleted() {
        GoalDto goalDto = new GoalDto();
        goalDto.setStatus("COMPLETED");
        goalDto.setSkillIds(List.of(1L, 2L, 3L, 4L, 5L));
        Goal goal = new Goal();
        goal.setStatus(GoalStatus.ACTIVE);

        User firstUser = new User();
        firstUser.setId(1L);

        when(goalRepository.findGoalById(1L)).thenReturn(goal);
        when(skillService.checkAmountSkillsInDB(goalDto.getSkillIds())).thenReturn(5);
        when(goalRepository.findUsersByGoalId(1L)).thenReturn(List.of(firstUser));

        goalService.updateGoal(1L, goalDto);

        verify(goalRepository, times(1)).findGoalById(1L);
        verify(skillService, times(1)).assignSkillToUser(1L, 1L);
        verify(skillService, times(1)).assignSkillToUser(2L, 1L);
        verify(skillService, times(1)).assignSkillToUser(3L, 1L);
        verify(skillService, times(1)).assignSkillToUser(4L, 1L);
        verify(skillService, times(1)).assignSkillToUser(5L, 1L);
    }

    @Test
    void testUpdateWhereUpdateSkillsInGoal() {
        GoalDto goalDto = new GoalDto();
        goalDto.setStatus("ACTIVE");
        goalDto.setSkillIds(List.of(1L, 2L, 3L, 4L, 5L));
        Goal goal = new Goal();
        goal.setStatus(GoalStatus.ACTIVE);

        User firstUser = new User();
        firstUser.setId(1L);

        when(goalRepository.findGoalById(1L)).thenReturn(goal);
        when(skillService.checkAmountSkillsInDB(goalDto.getSkillIds())).thenReturn(5);

        goalService.updateGoal(1L, goalDto);

        verify(goalRepository, times(1)).removeSkillsFromGoal(1L);
        verify(goalRepository, times(1)).addSkillToGoal(1L, 1L);
        verify(goalRepository, times(1)).addSkillToGoal(2L, 1L);
        verify(goalRepository, times(1)).addSkillToGoal(3L, 1L);
        verify(goalRepository, times(1)).addSkillToGoal(4L, 1L);
        verify(goalRepository, times(1)).addSkillToGoal(5L, 1L);
    }

    private Goal init(boolean param) {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setTitle("New");
        goal.setDescription("descr");
        List<Skill> skills = List.of(new Skill(), new Skill());
        goal.setSkillsToAchieve(skills);

        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(0);
        when(skillService.checkSkillsInDB(goal.getSkillsToAchieve())).thenReturn(param);
        return goal;
    }

    private GoalDto updateInit(GoalStatus status) {
        GoalDto goalDto = new GoalDto();
        goalDto.setSkillIds(List.of(1L, 2L, 3L, 4L, 5L));
        Goal goal = new Goal();
        goal.setStatus(status);

        when(goalRepository.findGoalById(1L)).thenReturn(goal);
        return goalDto;
    }
}