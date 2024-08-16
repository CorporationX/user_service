package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private GoalValidator goalValidator;

    @InjectMocks
    private GoalService goalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private GoalDto createGoalDto(String title, String description, Long parentGoalId, List<Long> skillIds) {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle(title);
        goalDto.setDescription(description);
        goalDto.setParentGoalId(parentGoalId);
        goalDto.setSkillIds(skillIds);
        return goalDto;
    }

    private Goal createGoal(Long id, String title, String description, GoalStatus status, List<Skill> skills) {
        Goal goal = new Goal();
        goal.setId(id);
        goal.setTitle(title);
        goal.setDescription(description);
        goal.setStatus(status);
        goal.setSkillsToAchieve(skills);
        return goal;
    }

    private Skill createSkill(Long id) {
        Skill skill = new Skill();
        skill.setId(id);
        return skill;
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private void setupMocksForCreateGoal(GoalDto goalDto, Goal savedGoal) {
        when(goalRepository.create(anyString(), anyString(), any(Long.class))).thenReturn(savedGoal);
        when(goalMapper.toDto(savedGoal)).thenReturn(goalDto);
    }

    @Test
    void createGoalSuccessTest() {
        GoalDto goalDto = createGoalDto("New Goal", "Goal Description", 1L, Arrays.asList(1L, 2L));
        Goal savedGoal = new Goal();
        savedGoal.setId(2L);

        setupMocksForCreateGoal(goalDto, savedGoal);

        GoalDto createdGoalDto = goalService.createGoal(1L, goalDto);

        assertNotNull(createdGoalDto);
        verify(goalValidator).createGoalValidator(1L, goalDto);
        verify(goalRepository).create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentGoalId());
        verify(goalRepository).addSkillToGoal(savedGoal.getId(), 1L);
        verify(goalRepository).addSkillToGoal(savedGoal.getId(), 2L);
    }

    @Test
    void createGoalThrowsExceptionWhenTitleIsNullTest() {
        GoalDto goalDto = new GoalDto();

        doThrow(new IllegalArgumentException("Goal must have a title"))
                .when(goalValidator).createGoalValidator(any(Long.class), any(GoalDto.class));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.createGoal(1L, goalDto);
        });

        assertEquals("Goal must have a title", exception.getMessage());
    }

    @Test
    void createGoalThrowsExceptionWhenTooManyActiveGoalsTest() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("New Goal");

        doThrow(new IllegalArgumentException("User cannot have more than 3 active goals"))
                .when(goalValidator).createGoalValidator(any(Long.class), any(GoalDto.class));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.createGoal(1L, goalDto);
        });

        assertEquals("User cannot have more than 3 active goals", exception.getMessage());
    }

    @Test
    void createGoalThrowsExceptionWhenSkillsDoNotExistTest() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("New Goal");
        goalDto.setSkillIds(Collections.singletonList(1L));

        doThrow(new IllegalArgumentException("One or more skills do not exist."))
                .when(goalValidator).createGoalValidator(any(Long.class), any(GoalDto.class));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.createGoal(1L, goalDto);
        });

        assertEquals("One or more skills do not exist.", exception.getMessage());
    }

    private void setupMocksForUpdateGoal(Goal goal, GoalDto goalDto) {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(skillService.countExisting(anyList())).thenReturn(2);
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(goalMapper.toDto(goal)).thenReturn(goalDto);
    }

    @Test
    void updateGoalSuccessTest() {
        Goal goal = createGoal(1L, "Old Title", "Old Description", GoalStatus.ACTIVE,
                Arrays.asList(createSkill(1L), createSkill(2L)));
        GoalDto goalDto = createGoalDto("New Title", "New Description", null, Arrays.asList(1L, 2L));
        goalDto.setStatus("active");

        setupMocksForUpdateGoal(goal, goalDto);

        GoalDto updatedGoalDto = goalService.updateGoal(1L, goalDto);

        assertNotNull(updatedGoalDto);
        assertEquals("New Title", updatedGoalDto.getTitle());
        assertEquals("New Description", updatedGoalDto.getDescription());
        assertEquals("active", updatedGoalDto.getStatus());

        verify(goalValidator).updateGoalValidator(1L, goalDto);
        verify(goalRepository).save(goal);
        verify(goalRepository).removeSkillsFromGoal(1L);
        verify(goalRepository).addSkillToGoal(1L, 1L);
        verify(goalRepository).addSkillToGoal(1L, 2L);
    }

    @Test
    void updateGoalThrowsExceptionWhenGoalNotFoundTest() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("New Title");

        doThrow(new IllegalArgumentException("Goal not found"))
                .when(goalValidator).updateGoalValidator(any(Long.class), any(GoalDto.class));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.updateGoal(1L, goalDto);
        });

        assertEquals("Goal not found", exception.getMessage());
    }

    @Test
    void updateGoalThrowsExceptionWhenGoalIsCompletedTest() {
        Goal goal = createGoal(1L, null, null, GoalStatus.COMPLETED, null);
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("New Title");

        doThrow(new IllegalArgumentException("Cannot update a completed goal"))
                .when(goalValidator).updateGoalValidator(any(Long.class), any(GoalDto.class));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.updateGoal(1L, goalDto);
        });

        assertEquals("Cannot update a completed goal", exception.getMessage());
    }

    @Test
    void updateGoalThrowsExceptionWhenTitleIsNullTest() {
        Goal goal = createGoal(1L, null, null, GoalStatus.ACTIVE, null);
        GoalDto goalDto = new GoalDto();

        doThrow(new IllegalArgumentException("Goal must have a title"))
                .when(goalValidator).updateGoalValidator(any(Long.class), any(GoalDto.class));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.updateGoal(1L, goalDto);
        });

        assertEquals("Goal must have a title", exception.getMessage());
    }

    @Test
    void updateGoalThrowsExceptionWhenSkillsDoNotExistTest() {
        Goal goal = createGoal(1L, null, null, GoalStatus.ACTIVE, Collections.singletonList(createSkill(1L)));
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("New Title");
        goalDto.setSkillIds(Collections.singletonList(1L));

        doThrow(new IllegalArgumentException("One or more skills do not exist"))
                .when(goalValidator).updateGoalValidator(any(Long.class), any(GoalDto.class));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.updateGoal(1L, goalDto);
        });

        assertEquals("One or more skills do not exist", exception.getMessage());
    }

    @Test
    void updateGoalCompletedStatusAssignsSkillsToUsersTest() {
        Goal goal = createGoal(1L, null, null, GoalStatus.ACTIVE, Arrays.asList(createSkill(1L), createSkill(2L)));
        GoalDto goalDto = createGoalDto("New Title", null, null, Arrays.asList(1L, 2L));
        goalDto.setStatus("completed");

        User user1 = createUser(1L);
        User user2 = createUser(2L);

        setupMocksForUpdateGoal(goal, goalDto);
        when(goalRepository.findUsersByGoalId(1L)).thenReturn(Arrays.asList(user1, user2));

        GoalDto updatedGoalDto = goalService.updateGoal(1L, goalDto);

        assertNotNull(updatedGoalDto);
        assertEquals("completed", updatedGoalDto.getStatus());

        verify(goalValidator).updateGoalValidator(1L, goalDto);
        verify(goalRepository).save(goal);
        verify(goalRepository).removeSkillsFromGoal(1L);
        verify(goalRepository).addSkillToGoal(1L, 1L);
        verify(goalRepository).addSkillToGoal(1L, 2L);
        verify(skillService).assignSkillToUser(1L, 1L);
        verify(skillService).assignSkillToUser(1L, 2L);
        verify(skillService).assignSkillToUser(2L, 1L);
        verify(skillService).assignSkillToUser(2L, 2L);
    }

    @Test
    void deleteGoalSuccessTest() {
        long goalId = 1L;
        Goal goal = createGoal(goalId, null, null, null, null);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        goalService.deleteGoal(goalId);

        verify(goalRepository).removeSkillsFromGoal(goalId);
        verify(goalRepository).deleteById(goalId);
    }
}