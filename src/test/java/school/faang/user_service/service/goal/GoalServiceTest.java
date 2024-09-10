package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalService goalService;

    private User user;
    private Goal goal;
    private Skill skill;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);

        skill = new Skill();
        skill.setId(10L);

        goal = new Goal();
        goal.setId(100L);
        goal.setTitle("Learning");
        goal.setSkillsToAchieve(new ArrayList<>(List.of(skill)));

    }

    @Test
    @DisplayName("Success create new goal")
    public void testCreateNewGoalIsSuccess() {
        when(goalRepository.countActiveGoalsPerUser(user.getId())).thenReturn(1);
        when(skillRepository.existsById(skill.getId())).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(goalRepository.save(goal)).thenReturn(goal);

        Goal newGoal = goalService.createGoal(user.getId(), goal);

        verify(goalRepository).save(goal);
        assertEquals(goal, newGoal);
    }

    @Test
    @DisplayName("Success update active goal")
    public void testUpdateActiveGoalDtoIsSuccess() {
        Goal existingGoal = new Goal();
        existingGoal.setId(100L);
        existingGoal.setSkillsToAchieve(new ArrayList<>());
        existingGoal.setUsers(new ArrayList<>());

        when(skillRepository.existsById(skill.getId())).thenReturn(true);
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(existingGoal));
        goal.setStatus(GoalStatus.ACTIVE);

        goalService.updateGoal(goal);

        verify(goalRepository).save(existingGoal);
        assertEquals(goal.getSkillsToAchieve(), existingGoal.getSkillsToAchieve());

    }

    @Test
    @DisplayName("Success update completed goal")
    public void testUpdateCompletedGoalDtoIsSuccess() {
        Goal existingGoal = new Goal();
        existingGoal.setId(100L);
        skill.setUsers(new ArrayList<>());
        existingGoal.setSkillsToAchieve(new ArrayList<>(List.of(skill)));
        existingGoal.setUsers(new ArrayList<>(List.of(user)));
        existingGoal.setStatus(GoalStatus.ACTIVE);

        when(skillRepository.existsById(skill.getId())).thenReturn(true);
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(existingGoal));
        goal.setStatus(GoalStatus.COMPLETED);

        goalService.updateGoal(goal);

        assertEquals(GoalStatus.COMPLETED, existingGoal.getStatus());
        assertTrue(skill.getUsers().contains(user));
        verify(goalRepository).save(existingGoal);
    }

    @Test
    @DisplayName("Incorrect goal title")
    public void testGoalTitleIsInvalid() {
        goal.setTitle(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> goalService.createGoal(user.getId(), goal)
        );

        String expectedMessage = "Title cannot be null or empty";
        String resultMessage = exception.getMessage();
        assertEquals(expectedMessage, resultMessage);
    }

    @Test
    @DisplayName("Incorrect amount active goals")
    public void testUserActiveGoalCountIsInvalid() {
        when(goalRepository.countActiveGoalsPerUser(user.getId())).thenReturn(3);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> goalService.createGoal(user.getId(), goal)
        );

        String expectedMessage = "The number of active user goals has been exceeded";
        String resultMessage = exception.getMessage();
        assertEquals(expectedMessage, resultMessage);
    }

    @Test
    @DisplayName("Incorrect goal skill")
    public void testGoalSkillsIsInvalid() {
        when(goalRepository.countActiveGoalsPerUser(user.getId())).thenReturn(1);
        when(skillRepository.existsById(skill.getId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> goalService.createGoal(user.getId(), goal)
        );

        String expectedMessage = "Skill does not exist";
        String resultMessage = exception.getMessage();
        assertEquals(expectedMessage, resultMessage);
    }

    @Test
    @DisplayName("Incorrect goalEntity status")
    public void updateGoalIsInvalid() {
        goal.setStatus(GoalStatus.COMPLETED);
        when(skillRepository.existsById(skill.getId())).thenReturn(true);
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> goalService.updateGoal(goal)
        );

        String expectedMessage = "It is impossible to change a completed goal";
        String resultMessage = exception.getMessage();
        assertEquals(expectedMessage, resultMessage);
    }
}
