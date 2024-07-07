package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.GoalDto;
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

    // test for createGoal
    @Test
    void createGoal_Success() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("New Goal");
        goalDto.setDescription("Goal Description");
        goalDto.setParentGoalId(1L);
        goalDto.setSkillIds(Arrays.asList(1L, 2L));

        Goal savedGoal = new Goal();
        savedGoal.setId(2L);
        when(goalRepository.create(anyString(), anyString(), any(Long.class))).thenReturn(savedGoal);
        when(goalMapper.toDto(savedGoal)).thenReturn(goalDto);

        GoalDto createdGoalDto = goalService.createGoal(1L, goalDto);

        assertNotNull(createdGoalDto);
        verify(goalValidator).createGoalValidator(1L, goalDto);
        verify(goalRepository).create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentGoalId());
        verify(goalRepository).addSkillToGoal(savedGoal.getId(), 1L);
        verify(goalRepository).addSkillToGoal(savedGoal.getId(), 2L);
    }

    @Test
    void createGoal_ThrowsException_WhenTitleIsNull() {
        GoalDto goalDto = new GoalDto();

        doThrow(new IllegalArgumentException("Goal must have a title"))
                .when(goalValidator).createGoalValidator(any(Long.class), any(GoalDto.class));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.createGoal(1L, goalDto);
        });

        assertEquals("Goal must have a title", exception.getMessage());
    }

    @Test
    void createGoal_ThrowsException_WhenTooManyActiveGoals() {
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
    void createGoal_ThrowsException_WhenSkillsDoNotExist() {
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

    // test for updateGoal
    @Test
    void updateGoal_Success() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setTitle("Old Title");
        goal.setDescription("Old Description");
        goal.setStatus(GoalStatus.ACTIVE);
        Skill skill1 = new Skill();
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setId(2L);
        goal.setSkillsToAchieve(Arrays.asList(skill1, skill2));

        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("New Title");
        goalDto.setDescription("New Description");
        goalDto.setStatus("active");
        goalDto.setSkillIds(Arrays.asList(1L, 2L));

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(skillService.countExisting(anyList())).thenReturn(2);
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(goalMapper.toDto(goal)).thenReturn(goalDto);

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
    void updateGoal_ThrowsException_WhenGoalNotFound() {
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
    void updateGoal_ThrowsException_WhenGoalIsCompleted() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setStatus(GoalStatus.COMPLETED);

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
    void updateGoal_ThrowsException_WhenTitleIsNull() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setStatus(GoalStatus.ACTIVE);

        GoalDto goalDto = new GoalDto();

        doThrow(new IllegalArgumentException("Goal must have a title"))
                .when(goalValidator).updateGoalValidator(any(Long.class), any(GoalDto.class));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.updateGoal(1L, goalDto);
        });

        assertEquals("Goal must have a title", exception.getMessage());
    }

    @Test
    void updateGoal_ThrowsException_WhenSkillsDoNotExist() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setStatus(GoalStatus.ACTIVE);
        Skill skill = new Skill();
        skill.setId(1L);
        goal.setSkillsToAchieve(Collections.singletonList(skill));

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
    void updateGoal_CompletedStatus_AssignsSkillsToUsers() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setStatus(GoalStatus.ACTIVE);
        Skill skill1 = new Skill();
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setId(2L);
        goal.setSkillsToAchieve(Arrays.asList(skill1, skill2));

        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("New Title");
        goalDto.setStatus("completed");
        goalDto.setSkillIds(Arrays.asList(1L, 2L));

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(skillService.countExisting(anyList())).thenReturn(2);
        when(goalMapper.toEntity(goalDto)).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(goalMapper.toDto(goal)).thenReturn(goalDto);
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

    // test for deleteGoal
    @Test
    void deleteGoal_Success() {
        long goalId = 1L;
        Goal goal = new Goal();
        goal.setId(goalId);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        goalService.deleteGoal(goalId);

        verify(goalRepository).removeSkillsFromGoal(goalId);
        verify(goalRepository).deleteById(goalId);
    }

    // look for method tests for filters in the FilterTest class
}