package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
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
import school.faang.user_service.exceptions.GoalOverflowException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private GoalService goalService;
    Goal goal;
    Goal goal2;
    Goal goal3;
    User user;
    User user2;
    User user3;

    @BeforeEach
    void init() {
        Skill skill1 = Skill.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();

        Skill skill2 = Skill.builder()
                .id(2L)
                .build();

        Skill skill3 = Skill.builder()
                .id(3L)
                .build();

        goal = Goal.builder()
                .id(1L)
                .title("title")
                .description("descriptional")
                .parent(goal3)
                .skillsToAchieve(Arrays.asList(skill1, skill2, skill3))
                .build();

        goal2 = Goal.builder()
                .id(1L)
                .skillsToAchieve(Arrays.asList(skill1, skill2))
                .build();

        goal3 = Goal.builder()
                .id(1L)
                .title("t")
                .description("d")
                .parent(goal2)
                .skillsToAchieve(Collections.singletonList(skill1))
                .build();

        user = User.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();

        user2 = User.builder()
                .id(1L)
                .goals(Collections.singletonList(goal2))
                .build();

        user3 = User.builder()
                .id(1L)
                .goals(Arrays.asList(goal, goal2, goal3))
                .build();
    }

    @Test
    @DisplayName("Missing target remove test")
    void testDeleteGoalById() {
        goalService.deleteGoal(goal.getId());
        verify(goalRepository).deleteById(goal.getId());
    }


    @Test
    @DisplayName("Test create goal and ")
    void testCreateGoal() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(1L, goal));
    }

    @Test
    @DisplayName("Test throwing an exception Goal Overflow Exception")
    public void shouldGoalOverflowException() {
        when(userRepository.findById(user3.getId())).thenReturn(Optional.of(user3));
        assertThrows(GoalOverflowException.class, () -> goalService.createGoal(1L, goal));
    }

    @Test
    @DisplayName("Test save skill and goal")
    public void shouldSaveIsActiveSkillAndSaveGoal() {
        when(skillService.checkActiveSkill(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        goalService.createGoal(user.getId(), goal3);
        skillService.saveAll(goal2.getSkillsToAchieve());

        verify(goalRepository).create("t", "d", goal2.getId());
        verify(skillService).saveAll(goal3.getSkillsToAchieve());
    }
}