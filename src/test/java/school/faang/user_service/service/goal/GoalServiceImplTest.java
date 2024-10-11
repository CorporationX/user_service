package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.GoalCompletedEventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillServiceImpl skillService;

    @Mock
    private GoalCompletedEventPublisher goalCompletedPublisher;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private GoalServiceImpl goalService;

    private User user;
    private Goal goal;
    private List<Skill> userSkills;
    private List<Skill> newSkills;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setGoals(new ArrayList<>());
        userSkills = new ArrayList<>();
        user.setSkills(userSkills);
        newSkills = new ArrayList<>();

        goal = new Goal();
        goal.setId(1L);
        goal.setStatus(GoalStatus.COMPLETED);
    }

    @Test
    public void testCompleteUserGoal_Success() {
        user.getGoals().add(goal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(skillRepository.findSkillsByGoalId(1L)).thenReturn(new ArrayList<>());

        goalService.completeUserGoal(1L, 1L);

        verify(skillService, times(1)).removeSkillsFromUser(eq(user), anyList());
        verify(skillService, times(1)).assignSkillsToUser(eq(user), anyList());
        verify(goalCompletedPublisher, times(1)).publish(any(GoalCompletedEventDto.class));
    }

    @Test
    public void testCompleteUserGoal_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            goalService.completeUserGoal(1L, 1L);
        });

        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    public void testCompleteUserGoal_GoalNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            goalService.completeUserGoal(1L, 1L);
        });

        assertEquals("Goal not found with id: 1", exception.getMessage());
    }

    @Test
    public void testCompleteUserGoal_UserDoesNotHaveGoal() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.completeUserGoal(1L, 1L);
        });

        assertEquals("User doesn't have the goal", exception.getMessage());
    }

    @Test
    public void testCompleteUserGoal_GoalNotCompleted() {
        goal.setStatus(GoalStatus.ACTIVE);
        user.getGoals().add(goal);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.completeUserGoal(1L, 1L);
        });

        assertEquals("Goal is not completed", exception.getMessage());
    }
}