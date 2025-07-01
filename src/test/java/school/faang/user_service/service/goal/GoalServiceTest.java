package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.CountActiveGoalMoreMaxException;
import school.faang.user_service.exception.goal.GoalAlreadyCompletedException;
import school.faang.user_service.exception.goal.GoalNotFoundException;
import school.faang.user_service.exception.skill.SkillNotFoundException;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.model.goal.GoalFilter;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Mock
    private UserContext userContext;
    @Spy
    private GoalValidator goalValidator;
    @Captor
    private ArgumentCaptor<Goal> goalCaptor;
    @InjectMocks
    private GoalService goalService;
    private Goal goal;
    private Long parentId;
    private List<Long> skillIds;
    private long userId;
    private int countActiveGoalsPerUser;

    @BeforeEach
    void setUp() {
        goal = new Goal();
        goal.setTitle("Test Goal");
        goal.setDescription("Test description");
        goal.setStatus(GoalStatus.ACTIVE);
        parentId = 2L;
        skillIds = List.of(3L, 4L, 5L);
        userId = 1L;
        countActiveGoalsPerUser = 3;
    }

    @Test
    public void testGetGoalByIdOrThrow_successfully() {
        goal.setId(12L);

        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));

        Goal returnGoal = goalService.getGoalById(goal.getId());

        verify(goalRepository, times(1)).findById(goal.getId());
        assertEquals(goal, returnGoal);
    }

    @Test
    public void testGetGoalByIdOrThrow_goalNotFound() {
        goal.setId(12L);

        when(goalRepository.findById(goal.getId())).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class, () -> goalService.getGoalById(goal.getId()));
        verify(goalRepository, times(1)).findById(goal.getId());
    }

    @Test
    public void testCreateGoal_savesGoalNonParent() {
        long goalId = 12L;

        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(countActiveGoalsPerUser);
        when(userService.getUserByIdOrThrow(eq(userId)))
                .thenAnswer(invocation -> {
                    Long userId = invocation.getArgument(0);
                    User user = new User();
                    user.setId(userId);
                    return user;
                });
        when(skillService.getSkillByIdOrThrow(longThat(skillId -> skillIds.contains(skillId))))
                .thenAnswer(invocation -> {
                    Long skillId = invocation.getArgument(0);
                    Skill skill = new Skill();
                    skill.setId(skillId);
                    return skill;
                });
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal savedGoal = invocation.getArgument(0);
            savedGoal.setId(goalId);
            return savedGoal;
        });

        Goal returnGoal = goalService.createGoal(goal, null, skillIds);


        verify(goalRepository, never()).findById(any());
        verify(goalRepository, times(1)).save(goalCaptor.capture());
        Goal saveGoal = goalCaptor.getValue();
        assertEquals(goalId, saveGoal.getId());
        assertNull(saveGoal.getParent());
        assertEquals(goal.getTitle(), saveGoal.getTitle());
        assertEquals(goal.getDescription(), saveGoal.getDescription());
        assertEquals(GoalStatus.ACTIVE, saveGoal.getStatus());
        assertTrue(saveGoal.getUsers().stream()
                .anyMatch(user -> user.getId().equals(userId)));
        assertTrue(saveGoal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList()
                .containsAll(skillIds));
        assertEquals(saveGoal, returnGoal);
    }

    @Test
    public void testCreateGoal_savesGoalWithParent() {
        long goalId = 12L;

        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(countActiveGoalsPerUser);
        when(userService.getUserByIdOrThrow(eq(userId)))
                .thenAnswer(invocation -> {
                    Long userId = invocation.getArgument(0);
                    User user = new User();
                    user.setId(userId);
                    return user;
                });
        when(goalRepository.findById(eq(parentId)))
                .thenAnswer(invocation -> {
                    Long goalParentId = invocation.getArgument(0);
                    Goal parentGoal = new Goal();
                    parentGoal.setId(goalParentId);
                    return Optional.of(parentGoal);
                });
        when(skillService.getSkillByIdOrThrow(longThat(skillId -> skillIds.contains(skillId))))
                .thenAnswer(invocation -> {
                    Long skillId = invocation.getArgument(0);
                    Skill skill = new Skill();
                    skill.setId(skillId);
                    return skill;
                });
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal savedGoal = invocation.getArgument(0);
            savedGoal.setId(goalId);
            return savedGoal;
        });

        Goal returnGoal = goalService.createGoal(goal, parentId, skillIds);

        verify(goalRepository, times(1)).findById(eq(parentId));
        verify(goalRepository, times(1)).save(goalCaptor.capture());
        Goal saveGoal = goalCaptor.getValue();
        assertEquals(goalId, saveGoal.getId());
        assertEquals(parentId, saveGoal.getParent().getId());
        assertEquals(goal.getTitle(), saveGoal.getTitle());
        assertEquals(goal.getDescription(), saveGoal.getDescription());
        assertEquals(GoalStatus.ACTIVE, saveGoal.getStatus());
        assertTrue(saveGoal.getUsers().stream()
                .anyMatch(user -> Objects.equals(user.getId(), userId)));
        assertTrue(saveGoal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList()
                .containsAll(skillIds));
        assertEquals(saveGoal, returnGoal);
    }

    @Test
    public void testCreateGoal_countActiveGoalMoreMax() {
        countActiveGoalsPerUser = 4;

        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(countActiveGoalsPerUser);

        assertThrows(CountActiveGoalMoreMaxException.class, () -> goalService.createGoal(goal, parentId, skillIds));
        verify(goalRepository, never()).save(goalCaptor.capture());
    }

    @Test
    public void testCreateGoal_nonExistingOwner() {
        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(countActiveGoalsPerUser);
        when(userService.getUserByIdOrThrow(userId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> goalService.createGoal(goal, parentId, skillIds));
        verify(goalRepository, never()).save(goalCaptor.capture());
    }

    @Test
    public void testCreateGoal_nonExistingParentGoal() {
        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(countActiveGoalsPerUser);
        when(userService.getUserByIdOrThrow(userId)).thenReturn(mock(User.class));
        when(goalRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class, () -> goalService.createGoal(goal, parentId, skillIds));
        verify(goalRepository, never()).save(goalCaptor.capture());
        verify(goalRepository, times(1)).findById(eq(parentId));
    }

    @Test
    public void testCreateGoal_nonExistingSkill() {
        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(countActiveGoalsPerUser);
        when(userService.getUserByIdOrThrow(userId)).thenReturn(mock(User.class));
        when(goalRepository.findById(parentId)).thenReturn(Optional.of(mock(Goal.class)));
        when(skillService.getSkillByIdOrThrow(longThat(skillId -> skillIds.contains(skillId))))
                .thenThrow(SkillNotFoundException.class);

        assertThrows(SkillNotFoundException.class, () -> goalService.createGoal(goal, parentId, skillIds));
        verify(goalRepository, never()).save(goalCaptor.capture());
    }

    @Test
    public void testUpdateGoal_savesGoalActive() {
        long goalId = 12L;

        when(skillService.getSkillByIdOrThrow(longThat(skillId -> skillIds.contains(skillId))))
                .thenAnswer(invocation -> {
                    Long skillId = invocation.getArgument(0);
                    Skill skill = new Skill();
                    skill.setId(skillId);
                    return skill;
                });
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal savedGoal = invocation.getArgument(0);
            savedGoal.setId(goalId);
            User owner = new User();
            owner.setId(userId);
            savedGoal.setUsers(List.of(owner));
            return savedGoal;
        });

        Goal returnGoal = goalService.updateGoal(goal, skillIds);

        verify(goalRepository, times(1)).save(goalCaptor.capture());
        Goal saveGoal = goalCaptor.getValue();
        verify(skillService, never()).assignSkillsToUsers(any(), any());
        assertEquals(goalId, saveGoal.getId());
        assertEquals(goal.getTitle(), saveGoal.getTitle());
        assertEquals(goal.getDescription(), saveGoal.getDescription());
        assertEquals(goal.getStatus(), saveGoal.getStatus());
        assertTrue(saveGoal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList()
                .containsAll(skillIds));
        assertEquals(saveGoal, returnGoal);
    }

    @Test
    public void testUpdateGoal_savesGoalCompleted() {
        long goalId = 12L;
        goal.setStatus(GoalStatus.COMPLETED);

        when(skillService.getSkillByIdOrThrow(longThat(skillId -> skillIds.contains(skillId))))
                .thenAnswer(invocation -> {
                    Long skillId = invocation.getArgument(0);
                    Skill skill = new Skill();
                    skill.setId(skillId);
                    return skill;
                });
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal savedGoal = invocation.getArgument(0);
            savedGoal.setId(goalId);
            User owner = new User();
            owner.setId(userId);
            savedGoal.setUsers(List.of(owner));
            return savedGoal;
        });

        Goal returnGoal = goalService.updateGoal(goal, skillIds);

        verify(goalRepository, times(1)).save(goalCaptor.capture());
        Goal saveGoal = goalCaptor.getValue();
        verify(skillService, times(1))
                .assignSkillsToUsers(
                        eq(skillIds),
                        argThat(userIds -> userIds.containsAll(
                                saveGoal.getUsers().stream()
                                        .map(User::getId)
                                        .toList()
                        )));
        assertEquals(goalId, saveGoal.getId());
        assertEquals(goal.getTitle(), saveGoal.getTitle());
        assertEquals(goal.getDescription(), saveGoal.getDescription());
        assertEquals(goal.getStatus(), saveGoal.getStatus());
        assertTrue(saveGoal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList()
                .containsAll(skillIds));
        assertEquals(saveGoal, returnGoal);
    }

    @Test
    public void testUpdateGoal_nonExistingSkill() {
        when(skillService.getSkillByIdOrThrow(longThat(skillId -> skillIds.contains(skillId))))
                .thenThrow(SkillNotFoundException.class);

        assertThrows(SkillNotFoundException.class, () -> goalService.updateGoal(goal, skillIds));
        verify(goalRepository, never()).save(goalCaptor.capture());
        verify(skillService, never()).assignSkillsToUsers(any(), any());
    }

    @Test
    public void testDeleteGoalById_goalDelete() {
        long goalId = 1L;
        Goal goal = new Goal();
        goal.setId(goalId);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertDoesNotThrow(() -> goalService.deleteGoalById(goalId));
        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    public void testDeleteGoalById_goalNotFound() {
        long goalId = 1L;
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class, () -> goalService.deleteGoalById(goalId));
        verify(goalRepository, never()).deleteById(goalId);
    }

    @Test
    public void testGetSubtasksByParentGoalId() {
        when(goalRepository.findByParent(parentId)).thenReturn(Stream.of(goal));

        List<Goal> goalsChild = goalService.getSubtasksByParentGoalId(parentId);

        verify(goalRepository, times(1)).findByParent(parentId);
        assertTrue(goalsChild.contains(goal));
    }

    @Test
    public void testGetGoalsByUserAndFilter() {
        GoalFilter filter = new GoalFilter(goal.getTitle(), goal.getStatus());

        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));

        List<Goal> filterGoals = goalService.getGoalsByUserAndFilter(filter);

        verify(goalRepository, times(1)).findGoalsByUserId(userId);
        assertTrue(filterGoals.contains(goal));
    }

    @Test
    public void testGetGoalByIdIfActiveElseThrow_successfully() {
        goal.setId(12L);

        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));

        Goal returnGoal = goalService.getGoalByIdIfActiveElseThrow(goal.getId());

        verify(goalRepository, times(1)).findById(goal.getId());
        assertEquals(goal, returnGoal);
    }

    @Test
    public void testGetGoalByIdIfActiveElseThrow_goalNotFound() {
        goal.setId(12L);

        when(goalRepository.findById(goal.getId())).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class,
                () -> goalService.getGoalByIdIfActiveElseThrow(goal.getId()));
        verify(goalRepository, times(1)).findById(goal.getId());
    }

    @Test
    public void testGetGoalByIdIfActiveElseThrow_goalAlreadyCompleted() {
        goal.setId(12L);
        goal.setStatus(GoalStatus.COMPLETED);

        when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));

        assertThrows(GoalAlreadyCompletedException.class,
                () -> goalService.getGoalByIdIfActiveElseThrow(goal.getId()));
        verify(goalRepository, times(1)).findById(goal.getId());
    }
}
