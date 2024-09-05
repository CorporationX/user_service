package school.faang.user_service.service.goal;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.Arrays;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private GoalService goalService;

    @Test
    @DisplayName("Empty and null goal title")
    public void testNullAndEmptyTitleIsInvalid() {
        GoalDto nullTitleGoal = new GoalDto();
        nullTitleGoal.setTitle(null);

        GoalDto emptyTitleGoal = new GoalDto();
        emptyTitleGoal.setTitle("    ");

        Long userId = 1L;

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> goalService.createGoal(userId, nullTitleGoal));

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> goalService.createGoal(userId, emptyTitleGoal));
    }

    @Test
    @DisplayName("Incorrect amount active goals")
    public void testUserActiveGoalCountIsInvalid() {
        Long userId = 1L;

        GoalDto goal = new GoalDto();
        goal.setTitle("learning");

        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);
        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> goalService.createGoal(userId, goal)
        );
    }

    @Test
    @DisplayName("Incorrect goal skill")
    public void testGoalSkillsIsInvalid() {
        Long userId = 1L;

        GoalDto goal = new GoalDto();
        goal.setTitle("learning");
        Long firstSkillId = 10L;
        Long secondSkillId = 11L;
        goal.setSkillIds(Arrays.asList(firstSkillId, secondSkillId));

        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        Mockito.when(skillService.isExistingSkill(firstSkillId)).thenReturn(true);
        Mockito.when(skillService.isExistingSkill(secondSkillId)).thenReturn(false);

        IllegalArgumentException exception = Assert.assertThrows(
                IllegalArgumentException.class,
                () -> goalService.createGoal(userId, goal));
    }

    @Test
    @DisplayName("Succsess create new goal")
    public void testCreateNewGoal() {
        Long userId = 1L;

        GoalDto goal = new GoalDto();
        goal.setTitle("Learning");

        Long firstSkillId = 10L;
        goal.setSkillIds(List.of(firstSkillId));

        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(1);
        Mockito.when(skillService.isExistingSkill(firstSkillId)).thenReturn(true);

        goalService.createGoal(userId, goal);
        Mockito.verify(goalRepository, Mockito.times(1))
                .create(goal.getTitle(), goal.getDescription(), goal.getParent());

        Mockito.verify(goalRepository, Mockito.times(1))
                .addSkillToGoal(firstSkillId, goal.getId());
    }
}
