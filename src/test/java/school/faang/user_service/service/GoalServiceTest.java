package school.faang.user_service.service;
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
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import school.faang.user_service.service.goal.GoalService;

class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Goal createGoal(Long id, String title, String description, GoalStatus status) {
        Goal goal = new Goal();
        goal.setId(id);
        goal.setTitle(title);
        goal.setDescription(description);
        goal.setStatus(status);
        return goal;
    }

    private GoalDto createGoalDto(Long id, String title, String description, GoalStatus status, List<Long> skillIds) {
        return new GoalDto(id, null, title, description, status, null, null, null, null, skillIds, null);
    }

    @Test
    void testCreateGoalSuccess() {
        Long userId = 1L;
        List<Long> skillIds = Arrays.asList(1L, 2L);
        GoalDto goalDto = createGoalDto(null, "New Goal", "Description", GoalStatus.ACTIVE, skillIds);
        Goal goal = new Goal();

        List<Skill> skills = Arrays.asList(
                Skill.builder().id(1L).title("Skill1").build(),
                Skill.builder().id(2L).title("Skill2").build()
        );

        when(goalMapper.goalDtoToGoal(goalDto)).thenReturn(goal);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        when(skillRepository.findAllById(skillIds)).thenReturn(skills);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        goalService.createGoal(userId, goalDto);

        verify(goalRepository, times(1)).save(goal);
        assertEquals(GoalStatus.ACTIVE, goal.getStatus());
    }

    @Test
    void testCreateGoalMaxActiveGoalsExceeded() {
        Long userId = 1L;
        GoalDto goalDto = createGoalDto(null, "Test Goal", "Description", GoalStatus.ACTIVE, Arrays.asList(1L, 2L));

        when(goalMapper.goalDtoToGoal(any(GoalDto.class))).thenReturn(new Goal());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(userId, goalDto));

        assertEquals("Пользователь не может иметь более 3 активных целей", exception.getMessage());
    }

    @Test
    void testCreateGoalEmptySkillList_ShouldThrowException() {
        Long userId = 1L;
        GoalDto goalDto = createGoalDto(null, "Test Goal", "Description", GoalStatus.ACTIVE, Collections.emptyList());

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(goalMapper.goalDtoToGoal(any(GoalDto.class))).thenReturn(new Goal());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(userId, goalDto));

        assertEquals("Навыки не могут быть пустыми", exception.getMessage());
    }

    @Test
    void testCreateGoalUserNotFound_ShouldThrowException() {
        Long userId = 1L;
        GoalDto goalDto = createGoalDto(null, "Test Goal", "Description", GoalStatus.ACTIVE, Arrays.asList(1L, 2L));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(userId, goalDto));

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void testCreateGoalSkillsNotFoundShouldThrowException() {
        Long userId = 1L;
        GoalDto goalDto = createGoalDto(null, "Test Goal", "Description", GoalStatus.ACTIVE, Arrays.asList(1L, 2L));

        when(goalMapper.goalDtoToGoal(any(GoalDto.class))).thenReturn(new Goal());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(skillRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(userId, goalDto));

        assertEquals("Некоторые навыки не существуют", exception.getMessage());
    }

    @Test
    void testUpdateGoalSuccess() {
        Long goalId = 1L;
        List<Long> skillIds = Arrays.asList(1L, 2L);
        GoalDto goalDto = createGoalDto(goalId, "Updated Goal", "Updated Description", GoalStatus.ACTIVE, skillIds);
        Goal existingGoal = createGoal(goalId, "Old Goal", "Old Description", GoalStatus.ACTIVE);

        List<Skill> skills = Arrays.asList(
                Skill.builder().id(1L).title("Skill1").build(),
                Skill.builder().id(2L).title("Skill2").build()
        );

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
        when(skillRepository.findAllById(skillIds)).thenReturn(skills);

        goalService.updateGoal(goalId, goalDto);

        verify(goalRepository, times(1)).save(existingGoal);
        assertEquals("Updated Goal", existingGoal.getTitle());
        assertEquals("Updated Description", existingGoal.getDescription());
    }

    @Test
    void testUpdateGoalGoalCompletedShouldThrowException() {
        Long goalId = 1L;
        GoalDto goalDto = createGoalDto(goalId, "Updated Goal", "Updated Description", GoalStatus.ACTIVE, Arrays.asList(1L, 2L));
        Goal existingGoal = createGoal(goalId, "Old Goal", "Old Description", GoalStatus.COMPLETED);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> goalService.updateGoal(goalId, goalDto));

        assertEquals("Нельзя обновлять завершенную цель", exception.getMessage());
        verify(goalRepository, never()).save(existingGoal);
    }

    @Test
    void testDeleteGoalSuccess() {
        long goalId = 1L;
        Goal existingGoal = createGoal(goalId, "Goal", "Description", GoalStatus.ACTIVE);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));

        goalService.deleteGoal(goalId);

        verify(goalRepository, times(1)).delete(existingGoal);
    }

    @Test
    void testDeleteGoalGoalNotFoundShouldThrowException() {
        long goalId = 1L;

        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> goalService.deleteGoal(goalId));

        assertEquals("Цель не найдена", exception.getMessage());
        verify(goalRepository, never()).delete(any(Goal.class));
    }

    @Test
    void testFindGoalsByUserId_Success() {
        long userId = 1L;

        Goal goal1 = createGoal(1L, "Goal Title 1", "Description 1", GoalStatus.ACTIVE);
        Goal goal2 = createGoal(2L, "Goal Title 2", "Description 2", GoalStatus.ACTIVE);

        GoalDto goalDto1 = createGoalDto(1L, "Goal Title 1", "Description 1", GoalStatus.ACTIVE, Arrays.asList(1L, 2L));
        GoalDto goalDto2 = createGoalDto(2L, "Goal Title 2", "Description 2", GoalStatus.ACTIVE, Arrays.asList(1L, 4L));

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Arrays.asList(goal1, goal2));
        when(goalMapper.goalToGoalDto(goal1)).thenReturn(goalDto1);
        when(goalMapper.goalToGoalDto(goal2)).thenReturn(goalDto2);

        List<GoalDto> result = goalService.findGoalsByUserId(userId, null);

        assertEquals(2, result.size());
        assertEquals("Goal Title 1", result.get(0).title());
        assertEquals("Goal Title 2", result.get(1).title());
        verify(goalRepository, times(1)).findGoalsByUserId(userId);
    }
}