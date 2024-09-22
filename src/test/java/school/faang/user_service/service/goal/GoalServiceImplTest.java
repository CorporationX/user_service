package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalDescriptionFilter;
import school.faang.user_service.service.goal.filter.GoalFilter;
import school.faang.user_service.service.goal.filter.GoalStatusFilter;
import school.faang.user_service.service.goal.filter.GoalTitleFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;
import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest extends CommonGoalTest {
    private static final int MAX_EXISTED_ACTIVE_GOALS = 3;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalServiceImpl goalService;

    private static List<GoalFilter> goalFilters;

    private Goal goal;

    private User user;

    private GoalFilterDto filterDto;

    @BeforeAll
    static void setupAll() {
        var goalDescriptionFilter = new GoalDescriptionFilter();
        var goalStatusFilter = new GoalStatusFilter();
        var goalTitleFilter = new GoalTitleFilter();
        goalFilters = Arrays.asList(goalDescriptionFilter, goalStatusFilter, goalTitleFilter);
    }

    @BeforeEach
    void setUpEach() {
        ReflectionTestUtils.setField(goalService, "goalFilters", goalFilters);
        ReflectionTestUtils.setField(goalService, "maxExistedActiveGoals", MAX_EXISTED_ACTIVE_GOALS);

        goal = createGoal();
        user = createUser();
        filterDto = createGoalFilterDto();
    }

    @Test
    void testCreateGoal_exception_hasNoExistingSkills() {
        List<Long> skillIds = List.of(SKILL_ID);
        when(skillRepository.countExisting(eq(skillIds))).thenReturn(0);

        assertThrows(BadRequestException.class, () ->
            goalService.createGoal(goal, USER_ID, null, List.of(SKILL_ID))
        );
    }

    @Test
    void testCreateGoal_exception_activeGoalsForUserMoreThanLimit() {
        List<Long> skillIds = List.of(SKILL_ID);
        when(skillRepository.countExisting(eq(skillIds))).thenReturn(1);
        when(goalRepository.countActiveGoalsPerUser(eq(USER_ID))).thenReturn(MAX_EXISTED_ACTIVE_GOALS);

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            goalService.createGoal(goal, USER_ID, null, List.of(SKILL_ID))
        );

        assertEquals("User 1 can have maximum 3 goals", exception.getMessage());
    }

    @Test
    void testCreateGoal_exception_parentGoalNotFound() {
        List<Long> skillIds = List.of(SKILL_ID);
        when(skillRepository.countExisting(eq(skillIds))).thenReturn(1);
        when(goalRepository.countActiveGoalsPerUser(eq(USER_ID))).thenReturn(MAX_EXISTED_ACTIVE_GOALS - 1);
        when(goalRepository.findById(eq(PARENT_GOAL_ID))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            goalService.createGoal(goal, USER_ID, PARENT_GOAL_ID, List.of(SKILL_ID))
        );

        assertEquals("Parent Goal 3 not found", exception.getMessage());
    }

    @Test
    void testCreateGoal_exception_userNotFound() {
        List<Long> skillIds = List.of(SKILL_ID);
        when(skillRepository.countExisting(eq(skillIds))).thenReturn(1);
        when(goalRepository.countActiveGoalsPerUser(eq(USER_ID))).thenReturn(MAX_EXISTED_ACTIVE_GOALS - 1);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            goalService.createGoal(goal, USER_ID, null, List.of(SKILL_ID))
        );

        assertEquals("User 1 not found", exception.getMessage());
    }

    @Test
    void testCreateGoal_success_ParentGoalNull_SkillIdsNull() {
        Goal createdGoal = createGoal();
        createdGoal.setId(GOAL_ID);
        when(goalRepository.countActiveGoalsPerUser(eq(USER_ID))).thenReturn(MAX_EXISTED_ACTIVE_GOALS - 1);
        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(user));
        when(goalRepository.save(any(Goal.class))).thenReturn(createdGoal);

        Goal result = goalService.createGoal(goal, USER_ID, null, null);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(createdGoal);
    }

    @Test
    void testCreateGoal_success_ParentGoalNotNull_SkillIdsNotNull() {
        List<Long> skillIds = List.of(SKILL_ID);
        Goal parentGoal = createGoal();
        Goal createdGoal = createGoal();
        createdGoal.setId(GOAL_ID);
        when(skillRepository.countExisting(eq(skillIds))).thenReturn(1);
        when(goalRepository.countActiveGoalsPerUser(eq(USER_ID))).thenReturn(MAX_EXISTED_ACTIVE_GOALS - 1);
        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(user));
        when(goalRepository.save(any(Goal.class))).thenReturn(createdGoal);
        when(skillRepository.findByIds(eq(skillIds))).thenReturn(List.of(Skill.builder().id(SKILL_ID).build()));
        when(goalRepository.findById(eq(PARENT_GOAL_ID))).thenReturn(Optional.of(parentGoal));

        Goal result = goalService.createGoal(goal, USER_ID, PARENT_GOAL_ID, List.of(SKILL_ID));

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(createdGoal);
    }

    @Test
    void testUpdateGoal_exception_hasNoExistingSkills() {
        List<Long> skillIds = List.of(SKILL_ID);
        when(skillRepository.countExisting(eq(skillIds))).thenReturn(0);

        assertThrows(BadRequestException.class, () ->
            goalService.updateGoal(goal, List.of(SKILL_ID))
        );
    }

    @Test
    void testUpdateGoal_exception_goalAlreadyCompleted() {
        List<Long> skillIds = List.of(SKILL_ID);
        Goal existedGoal = createGoal();
        existedGoal.setStatus(COMPLETED);
        when(skillRepository.countExisting(eq(skillIds))).thenReturn(1);
        when(goalRepository.findById(eq(GOAL_ID))).thenReturn(Optional.of(existedGoal));

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            goalService.updateGoal(goal, List.of(SKILL_ID))
        );

        assertEquals("You cannot update Goal 2 with the COMPLETED status.", exception.getMessage());
    }

    @Test
    void testUpdateGoal_success() {
        List<Long> skillIds = List.of(SKILL_ID);
        Goal existedGoal = createGoal();
        Goal createdGoal = createGoal();
        createdGoal.setId(GOAL_ID);
        when(skillRepository.countExisting(eq(skillIds))).thenReturn(1);
        when(goalRepository.findById(eq(GOAL_ID))).thenReturn(Optional.of(existedGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(createdGoal);

        Goal result = goalService.updateGoal(goal, List.of(SKILL_ID));

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(createdGoal);
    }

    @Test
    void testDeleteGoal_success() {
        when(goalRepository.findGoalsByParent(eq(GOAL_ID))).thenReturn(List.of());
        doNothing().when(goalRepository).deleteById(GOAL_ID);

        goalService.deleteGoal(GOAL_ID);
    }

    @Test
    void testFindSubtasksByGoalId_notFoundSubGoals() {
        when(goalRepository.findGoalsByParent(PARENT_GOAL_ID)).thenReturn(List.of());

        List<Goal> result = goalService.findSubGoalsByParentGoalId(PARENT_GOAL_ID, filterDto);
        assertEquals(0, result.size());
    }

    @Test
    void testFindSubtasksByGoalId_foundOneSubgoal() {
        Goal subGoalToFound = createGoal();
        subGoalToFound.setUsers(List.of(User.builder().id(USER_ID).build()));
        subGoalToFound.setStatus(ACTIVE);

        Goal subGoalToNotFound = createGoal();
        subGoalToNotFound.setDescription("Become a software engineer");
        subGoalToNotFound.setUsers(List.of(User.builder().id(USER_ID).build()));
        subGoalToNotFound.setStatus(ACTIVE);

        when(goalRepository.findGoalsByParent(eq(PARENT_GOAL_ID))).thenReturn(List.of(subGoalToFound, subGoalToNotFound));

        List<Goal> result = goalService.findSubGoalsByParentGoalId(PARENT_GOAL_ID, filterDto);
        assertEquals(1, result.size());
        assertEquals(goal.getId(), result.get(0).getId());
    }

    @Test
    void testGetGoalsByUser_notFoundSubGoals() {
        when(goalRepository.findGoalsByUserId(USER_ID)).thenReturn(Stream.of());

        List<Goal> result = goalService.findGoalsByUserId(USER_ID, filterDto);
        assertEquals(0, result.size());
    }

    @Test
    void testGetGoalsByUser_foundOneSubgoal() {
        Goal subGoalToFound = createGoal();
        subGoalToFound.setUsers(List.of(User.builder().id(USER_ID).build()));
        subGoalToFound.setStatus(ACTIVE);

        Goal subGoalToNotFound = createGoal();
        subGoalToNotFound.setDescription("Become a software engineer");
        subGoalToNotFound.setUsers(List.of(User.builder().id(USER_ID).build()));
        subGoalToNotFound.setStatus(ACTIVE);

        when(goalRepository.findGoalsByUserId(eq(USER_ID))).thenReturn(Stream.of(subGoalToFound, subGoalToNotFound));

        List<Goal> result = goalService.findGoalsByUserId(USER_ID, filterDto);

        assertEquals(1, result.size());
        assertEquals(goal.getId(), result.get(0).getId());
    }

    @Test
    public void deleteGoalAndUnlinkChildrenSuccess() {
        Goal child1 = goal.getChildrenGoals().get(0);
        Goal child2 = goal.getChildrenGoals().get(1);

        goalService.deleteGoalAndUnlinkChildren(goal);

        verify(goalRepository, times(2)).save(any(Goal.class));
        verify(goalRepository).delete(goal);

        assertNull(child1.getParent());
        assertNull(child2.getParent());
    }
}