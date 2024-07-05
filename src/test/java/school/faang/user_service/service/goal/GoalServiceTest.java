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
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private GoalService goalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // test for createGoal
    @Test
    void createGoal_Success() {
        Goal goal = new Goal();
        goal.setTitle("New Goal");
        goal.setDescription("Goal Description");
        goal.setId(2L);
        Goal parentGoal = new Goal();
        parentGoal.setId(1L);
        goal.setParent(parentGoal);
        Skill skill1 = new Skill();
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setId(2L);
        goal.setSkillsToAchieve(Arrays.asList(skill1, skill2));

        when(goalRepository.countActiveGoalsPerUser(any(Long.class))).thenReturn(2);
        when(skillService.countExisting(anyList())).thenReturn(2);
        when(goalRepository.create(anyString(), anyString(), any(Long.class))).thenReturn(goal);

        Goal createdGoal = goalService.createGoal(1L, goal);

        assertNotNull(createdGoal);
        verify(goalRepository).create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
        verify(goalRepository).addSkillToGoal(createdGoal.getId(), 1L);
        verify(goalRepository).addSkillToGoal(createdGoal.getId(), 2L);
    }

    @Test
    void createGoal_ThrowsException_WhenTitleIsNull() {
        Goal goal = new Goal();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.createGoal(1L, goal);
        });

        assertEquals("Goal must have a title", exception.getMessage());
    }

    @Test
    void createGoal_ThrowsException_WhenTooManyActiveGoals() {
        Goal goal = new Goal();
        goal.setTitle("New Goal");

        when(goalRepository.countActiveGoalsPerUser(any(Long.class))).thenReturn(3);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.createGoal(1L, goal);
        });

        assertEquals("User cannot have more than 3 active goals", exception.getMessage());
    }

    @Test
    void createGoal_ThrowsException_WhenSkillsDoNotExist() {
        Goal goal = new Goal();
        goal.setTitle("New Goal");
        Skill skill = new Skill();
        skill.setId(1L);
        goal.setSkillsToAchieve(Collections.singletonList(skill));

        when(goalRepository.countActiveGoalsPerUser(any(Long.class))).thenReturn(1);
        when(skillService.countExisting(anyList())).thenReturn(0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            goalService.createGoal(1L, goal);
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
        goalDto.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(skillService.countExisting(anyList())).thenReturn(2);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        Goal updatedGoal = goalService.updateGoal(1L, goalDto);

        assertNotNull(updatedGoal);
        assertEquals("New Title", updatedGoal.getTitle());
        assertEquals("New Description", updatedGoal.getDescription());
        assertEquals(GoalStatus.ACTIVE, updatedGoal.getStatus());

        verify(goalRepository).save(goal);
        verify(goalRepository).removeSkillsFromGoal(1L);
        verify(goalRepository).addSkillToGoal(1L, 1L);
        verify(goalRepository).addSkillToGoal(1L, 2L);
    }

    @Test
    void updateGoal_ThrowsException_WhenGoalNotFound() {
        GoalDto goalDto = new GoalDto();
        goalDto.setTitle("New Title");

        when(goalRepository.findById(any(Long.class))).thenReturn(Optional.empty());

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

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

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

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

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

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(skillService.countExisting(anyList())).thenReturn(0);

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
        goalDto.setStatus(GoalStatus.COMPLETED);

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(skillService.countExisting(anyList())).thenReturn(2);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(goalRepository.findUsersByGoalId(1L)).thenReturn(Arrays.asList(user1, user2));

        Goal updatedGoal = goalService.updateGoal(1L, goalDto);

        assertNotNull(updatedGoal);
        assertEquals(GoalStatus.COMPLETED, updatedGoal.getStatus());

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

        goalService.deleteGoal(goalId);

        verify(goalRepository).removeSkillsFromGoal(goalId);
        verify(goalRepository).deleteById(goalId);
    }
}