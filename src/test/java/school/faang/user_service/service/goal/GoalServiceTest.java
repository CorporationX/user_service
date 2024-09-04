package school.faang.user_service.service.goal;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Arrays;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private GoalService goalService;

    @Test
    @DisplayName("Empty and null goal title")
    public void testNullAndEmptyTitleIsInvalid() {
        GoalDto nullTitleGoal = new GoalDto();
        nullTitleGoal.setTitle(null);

        GoalDto emptyTitleGoal = new GoalDto();
        emptyTitleGoal.setTitle("   ");

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

        SkillDto firstSkill = new SkillDto();
        firstSkill.setId(1L);
        firstSkill.setTitle("Java");

        SkillDto secondSkill = new SkillDto();
        secondSkill.setId(1L);
        secondSkill.setTitle("Archery");

        goal.setSkills(Arrays.asList(firstSkill, secondSkill));

        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        Mockito.when(skillRepository.existsByTitle("Java")).thenReturn(true);
        Mockito.when(skillRepository.existsByTitle("Archery")).thenReturn(false);

        IllegalArgumentException exception = Assert.assertThrows(
                IllegalArgumentException.class,
                () -> goalService.createGoal(userId, goal));

        Assertions.assertEquals("Archery skill does not exist", exception.getMessage());
    }

    @Test
    @DisplayName("Succsess create new goal")
    public void testCreateNewGoal() {
        Long userId = 1L;

        GoalDto goal = new GoalDto();
        goal.setTitle("Learning");

        SkillDto firstSkill = new SkillDto();
        firstSkill.setId(1L);
        firstSkill.setTitle("Java");

        goal.setSkills(List.of(firstSkill));

        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(1);
        Mockito.when(skillRepository.existsByTitle("Java")).thenReturn(true);

        goalService.createGoal(userId, goal);
        Mockito.verify(goalRepository, Mockito.times(1))
                .create(goal.getTitle(), goal.getDescription(), goal.getParent());

        Mockito.verify(goalRepository, Mockito.times(1))
                .addSkillToGoal(firstSkill.getId(), goal.getId());
    }
}
