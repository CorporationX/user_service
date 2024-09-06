package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;
import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest extends CommonGoalTest {
    private static final int MAX_EXISTED_ACTIVE_GOALS = 3;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private GoalValidator goalValidator;

    private GoalDto goalDto;

    private Goal goal;

    @BeforeEach
    void setUpEach() {
        ReflectionTestUtils.setField(goalValidator, "maxExistedActiveGoals", MAX_EXISTED_ACTIVE_GOALS);

        goalDto = GoalDto.builder()
            .userId(USER_ID)
            .build();

        goal = Goal.builder()
            .status(ACTIVE)
            .build();
    }

    @Test
    void testValidateCreation_userNotFound() {
        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.empty());

        ResourceNotFoundException userNotFoundException = assertThrows(ResourceNotFoundException.class, () ->
            goalValidator.validateCreation(goalDto)
        );

        assertEquals("User 1 not found", userNotFoundException.getMessage());
    }

    @Test
    void testValidateCreation_activeGoalsForUserMoreThanLimit() {
        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(User.builder().build()));
        when(goalRepository.countActiveGoalsPerUser(eq(USER_ID))).thenReturn(MAX_EXISTED_ACTIVE_GOALS);

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            goalValidator.validateCreation(goalDto)
        );

        assertEquals("User 1 can have maximum 3 goals", exception.getMessage());
    }

    @Test
    void testValidateCreation_hasNoExistingSkills() {
        goalDto.setSkillIds(List.of(1L, 2L, 3L));

        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(User.builder().build()));

        when(skillRepository.countExisting(List.of(1L, 2L, 3L))).thenReturn(2);

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            goalValidator.validateCreation(goalDto)
        );

        assertEquals("Skills from request are not presented in DB", exception.getMessage());
    }

    @Test
    void testValidateCreation_noExceptions_skillsTheSameAInDb() {
        goalDto.setSkillIds(List.of(1L, 2L, 3L));

        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(User.builder().build()));
        when(goalRepository.countActiveGoalsPerUser(eq(USER_ID))).thenReturn(MAX_EXISTED_ACTIVE_GOALS - 1);

        when(skillRepository.countExisting(List.of(1L, 2L, 3L))).thenReturn(3);

        assertDoesNotThrow(() -> goalValidator.validateCreation(goalDto));
    }

    @Test
    void testValidateCreation_noExceptions_skillsNull() {
        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(User.builder().build()));
        when(goalRepository.countActiveGoalsPerUser(eq(USER_ID))).thenReturn(MAX_EXISTED_ACTIVE_GOALS - 1);

        assertDoesNotThrow(() -> goalValidator.validateCreation(goalDto));

        verify(skillRepository, times(0)).countExisting(anyList());
    }

    @Test
    void testValidateUpdating_userNotFound() {
        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.empty());

        ResourceNotFoundException userNotFoundException = assertThrows(ResourceNotFoundException.class, () ->
            goalValidator.validateUpdating(goalDto)
        );

        assertEquals("User 1 not found", userNotFoundException.getMessage());
    }

    @Test
    void testValidateUpdating_activeGoalsForUserMoreThanLimit() {
        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(User.builder().build()));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            goalValidator.validateUpdating(goalDto)
        );

        assertEquals("Goal null not found", exception.getMessage());
    }

    @Test
    void testValidateUpdating_hasNoExistingSkills() {
        goalDto.setSkillIds(List.of(1L, 2L, 3L));

        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(User.builder().build()));

        when(skillRepository.countExisting(List.of(1L, 2L, 3L))).thenReturn(2);

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            goalValidator.validateUpdating(goalDto)
        );

        assertEquals("Skills from request are not presented in DB", exception.getMessage());
    }

    @Test
    void testValidateUpdating_goalNotFound() {
        goalDto.setGoalId(GOAL_ID);

        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(User.builder().build()));
        when(goalRepository.findById(eq(GOAL_ID))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            goalValidator.validateUpdating(goalDto)
        );

        assertEquals("Goal " + GOAL_ID + " not found", exception.getMessage());
    }

    @Test
    void testValidateUpdating_notAllowedToUpdateCompletedGoal() {
        goalDto.setGoalId(GOAL_ID);
        goal.setStatus(COMPLETED);

        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(User.builder().build()));
        when(goalRepository.findById(eq(GOAL_ID))).thenReturn(Optional.of(goal));

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            goalValidator.validateUpdating(goalDto)
        );

        assertEquals("You cannot update Goal " + GOAL_ID + " with the COMPLETED status.", exception.getMessage());
    }

    @Test
    void testValidateUpdating_makeGoalCompletedWithEmptySkills() {
        goalDto.setGoalId(GOAL_ID);
        goalDto.setStatus(COMPLETED);

        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(User.builder().build()));
        when(goalRepository.findById(eq(GOAL_ID))).thenReturn(Optional.of(goal));

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
            goalValidator.validateUpdating(goalDto)
        );

        assertEquals("You cannot complete Goal " + GOAL_ID + " with empty list of Goals.", exception.getMessage());
    }

    @Test
    void testValidateUpdating_makeGoalCompletedWithSkills() {
        goalDto.setGoalId(GOAL_ID);
        goalDto.setStatus(COMPLETED);
        goalDto.setSkillIds(List.of(SKILL_ID));

        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(User.builder().build()));
        when(goalRepository.findById(eq(GOAL_ID))).thenReturn(Optional.of(goal));
        when(skillRepository.countExisting(List.of(SKILL_ID))).thenReturn(1);

        assertDoesNotThrow(() -> goalValidator.validateUpdating(goalDto));
    }

    @Test
    void testValidateUpdating_parentGoalNotPresentedInDb() {
        goalDto.setGoalId(GOAL_ID);
        goalDto.setStatus(COMPLETED);
        goalDto.setSkillIds(List.of(SKILL_ID));
        goalDto.setParentGoalId(PARENT_GOAL_ID);

        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(User.builder().build()));
        when(skillRepository.countExisting(List.of(SKILL_ID))).thenReturn(1);
        when(goalRepository.findById(eq(PARENT_GOAL_ID))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
            goalValidator.validateUpdating(goalDto)
        );

        assertEquals("Parent Goal " + PARENT_GOAL_ID + " not found", exception.getMessage());
    }

    @Test
    void testValidateUpdating_parentGoalPresentedInDb() {
        goalDto.setGoalId(GOAL_ID);
        goalDto.setStatus(COMPLETED);
        goalDto.setSkillIds(List.of(SKILL_ID));
        goalDto.setParentGoalId(PARENT_GOAL_ID);

        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(User.builder().build()));
        when(skillRepository.countExisting(List.of(SKILL_ID))).thenReturn(1);
        when(goalRepository.findById(eq(PARENT_GOAL_ID))).thenReturn(Optional.of(Goal.builder().id(PARENT_GOAL_ID).build()));
        when(goalRepository.findById(eq(GOAL_ID))).thenReturn(Optional.of(goal));

        assertDoesNotThrow(() -> goalValidator.validateUpdating(goalDto));
    }
}