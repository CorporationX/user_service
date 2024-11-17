package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalFilter;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoalServiceTest {
    @InjectMocks
    private GoalService goalService;

    @Mock
    private UserService userService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private List<GoalFilter> goalFilters;

    @Spy
    private GoalMapper goalMapper;

    @Mock
    private GoalValidator goalValidation;

    private GoalDto goal;
    private GoalDto secondGoal;
    private Goal goalEntity;
    private Goal secondGoalEntity;

    @BeforeEach
    void setUp() {
        goal = new GoalDto();
        goal.setId(1L);
        goal.setTitle("Test Goal");
        goal.setDescription("This is a test goal");
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setSkillIds(List.of(1L, 2L, 3L));

        secondGoal = new GoalDto();
        secondGoal.setId(1L);
        secondGoal.setTitle("Test Goal");
        secondGoal.setDescription("This is a test goal");
        secondGoal.setStatus(GoalStatus.ACTIVE);
        secondGoal.setSkillIds(List.of(1L, 2L, 3L));

        goalEntity = new Goal();
        goalEntity.setId(goal.getId());
        goalEntity.setTitle(goal.getTitle());
        goalEntity.setDescription(goal.getDescription());
        goalEntity.setStatus(goal.getStatus());

        secondGoalEntity = new Goal();
        secondGoalEntity.setId(goal.getId());
        secondGoalEntity.setTitle(goal.getTitle() + " 2");
        secondGoalEntity.setDescription(goal.getDescription() + " 2");
        secondGoalEntity.setStatus(goal.getStatus());
    }

    @Test
    public void testCreateGoalSuccessCreated() {
        Long userId = 1L;

        when(userService.getUserById(userId)).thenReturn(Optional.of(new User()));
        when(goalMapper.toEntity(goal)).thenReturn(goalEntity);
        when(goalRepository.save(goalEntity)).thenReturn(goalEntity);
        when(goalMapper.toDto(goalEntity)).thenReturn(goal);

        GoalDto createdGoal = goalService.createGoal(userId, goal);

        assertNotNull(createdGoal);
        assertEquals("Test Goal", createdGoal.getTitle());
        assertEquals("This is a test goal", createdGoal.getDescription());
        verify(goalValidation, times(1)).validateGoalRequest(userId, goal, true);
        verify(goalRepository, times(1)).save(goalEntity);
    }

    @Test
    void testCreateGoalWithParentGoal() {
        Long userId = 1L;
        GoalDto goal = new GoalDto();
        goal.setParentGoalId(2L);

        Goal goalEntity = new Goal();
        Goal parentGoal = new Goal();
        parentGoal.setId(2L);

        Optional<User> user = Optional.of(new User());
        user.get().setId(userId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(goalMapper.toEntity(goal)).thenReturn(goalEntity);
        when(goalRepository.findById(2L)).thenReturn(Optional.of(parentGoal));
        when(goalRepository.save(goalEntity)).thenReturn(goalEntity);
        when(goalMapper.toDto(goalEntity)).thenReturn(goal);

        GoalDto createdGoal = goalService.createGoal(userId, goal);

        assertNotNull(createdGoal);
        verify(goalRepository, times(1)).save(goalEntity);
    }

    @Test
    void testCreateGoalWithSkills() {
        Long userId = 1L;
        Skill firstSkill = new Skill();
        firstSkill.setId(1L);
        Skill secondSkill = new Skill();
        secondSkill.setId(2L);
        goal.setSkillIds(List.of(firstSkill.getId(), secondSkill.getId()));

        when(userService.getUserById(userId)).thenReturn(Optional.of(new User()));
        when(goalMapper.toEntity(goal)).thenReturn(goalEntity);
        when(skillService.getSkillById(firstSkill.getId())).thenReturn(firstSkill);
        when(skillService.getSkillById(secondSkill.getId())).thenReturn(secondSkill);
        when(goalRepository.save(goalEntity)).thenReturn(goalEntity);
        when(goalMapper.toDto(goalEntity)).thenReturn(goal);

        GoalDto createdGoal = goalService.createGoal(userId, goal);

        assertNotNull(createdGoal);
        assertEquals(2, goalEntity.getSkillsToAchieve().size());
        verify(skillService, times(1)).getSkillById(firstSkill.getId());
        verify(skillService, times(1)).getSkillById(secondSkill.getId());
        verify(goalRepository).save(goalEntity);
    }

    @Test
    void testCreateGoalValidationFails() {
        Long userId = 1L;
        doThrow(new DataValidationException("Validation failed")).when(goalValidation).validateGoalRequest(userId, goal, true);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalService.createGoal(userId, goal));
        assertEquals("Validation failed", exception.getMessage());
        verify(goalValidation).validateGoalRequest(userId, goal, true);
        verify(goalRepository, never()).save(any());
    }

    @Test
    void testUpdateGoalSuccessUpdated() {
        Long userId = 1L;
        when(goalRepository.findById(goal.getId())).thenReturn(Optional.ofNullable(goalEntity));
        when(goalMapper.toDto(goalEntity)).thenReturn(goal);

        GoalDto updatedGoal = goalService.updateGoal(userId, goal);

        assertNotNull(updatedGoal);
        assertEquals("Test Goal", updatedGoal.getTitle());
        assertEquals("This is a test goal", updatedGoal.getDescription());
        verify(goalValidation).validateGoalRequest(userId, goal, false);
        verify(goalRepository).save(goalEntity);
    }

    @Test
    void testUpdateGoalWithMentor() {
        Long userId = 1L;
        Long mentorId = 2L;
        goal.setMentorId(mentorId);
        User mentor = new User();
        mentor.setId(mentorId);

        when(goalRepository.findById(goal.getId())).thenReturn(Optional.ofNullable(goalEntity));
        when(userService.getUserById(mentorId)).thenReturn(Optional.of(mentor));
        when(goalMapper.toDto(goalEntity)).thenReturn(goal);

        GoalDto updatedGoal = goalService.updateGoal(userId, goal);

        assertNotNull(updatedGoal);
        assertEquals(mentorId, goalEntity.getMentor().getId());
        verify(userService, times(1)).getUserById(mentorId);
        verify(goalRepository, times(1)).save(goalEntity);
    }

    @Test
    void testUpdateGoalWithParentGoal() {
        Long userId = 1L;
        Long parentGoalId = 3L;
        goal.setParentGoalId(parentGoalId);
        Goal parentGoal = new Goal();
        parentGoal.setId(parentGoalId);

        when(goalRepository.findById(goal.getId())).thenReturn(Optional.ofNullable(goalEntity));
        when(goalRepository.findById(parentGoalId)).thenReturn(Optional.of(parentGoal));
        when(goalMapper.toDto(goalEntity)).thenReturn(goal);

        GoalDto updatedGoal = goalService.updateGoal(userId, goal);

        assertNotNull(updatedGoal);
        assertEquals(parentGoalId, goalEntity.getParent().getId());
        verify(goalRepository).findById(parentGoalId);
        verify(goalRepository).save(goalEntity);
    }

    @Test
    void testNotFoundUpdatedGoal() {
        Long userId = 1L;
        when(goalRepository.findById(goal.getId())).thenReturn(null);

        assertThrows(NullPointerException.class, () -> goalService.updateGoal(userId, goal));
        verify(goalRepository, never()).save(any());
    }

    @Test
    void testValidationFailsByUpdateGoal() {
        Long userId = 1L;
        doThrow(new DataValidationException("Validation failed")).when(goalValidation).validateGoalRequest(userId, goal, false);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalService.updateGoal(userId, goal));
        assertEquals("Validation failed", exception.getMessage());
        verify(goalValidation).validateGoalRequest(userId, goal, false);
        verify(goalRepository, never()).save(any());
    }

    @Test
    void testSuccessDeleteGoal() {
        long goalId = 1L;

        doNothing().when(goalRepository).deleteById(goalId);

        goalService.deleteGoal(goalId);

        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    void testGetGoalsByUserWithFilters() {
        long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();
        GoalFilter mockFilter = mock(GoalFilter.class);

        List<Goal> goals = List.of(goalEntity, secondGoalEntity);

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals);
        when(goalFilters.stream()).thenReturn(Stream.of(mockFilter));
        when(mockFilter.isApplicable(filters)).thenReturn(true);
        when(mockFilter.apply(any(), eq(filters))).thenReturn(goals.stream());
        when(goalMapper.toDto(goalEntity)).thenReturn(goal);
        when(goalMapper.toDto(secondGoalEntity)).thenReturn(goal);

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertEquals(2, result.size());
        assertEquals("Test Goal", result.get(0).getTitle());
        assertEquals("Test Goal", result.get(1).getTitle());

        verify(goalRepository).findGoalsByUserId(userId);
        verify(goalFilters).stream();
        verify(mockFilter).apply(any(), eq(filters));
        verify(goalMapper).toDto(goalEntity);
        verify(goalMapper).toDto(secondGoalEntity);
    }

    @Test
    void testGetGoalsByUserNoGoals() {
        long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(new ArrayList<>());

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertTrue(result.isEmpty());
        verify(goalRepository).findGoalsByUserId(userId);
        verify(goalFilters).stream();
        verifyNoInteractions(goalMapper);
    }

    @Test
    void testGetGoalsByUser_NoApplicableFilters() {
        long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();
        GoalFilter mockFilter = mock(GoalFilter.class);

        List<Goal> goals = List.of(goalEntity, secondGoalEntity);

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals);
        when(goalFilters.stream()).thenReturn(Stream.of(mockFilter));
        when(mockFilter.isApplicable(filters)).thenReturn(false);
        when(goalMapper.toDto(goalEntity)).thenReturn(goal);
        when(goalMapper.toDto(secondGoalEntity)).thenReturn(secondGoal);

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertEquals(2, result.size());
        assertEquals("Test Goal", result.get(0).getTitle());
        assertEquals("Test Goal", result.get(1).getTitle());

        verify(goalRepository).findGoalsByUserId(userId);
        verify(goalFilters).stream();
        verify(mockFilter, never()).apply(any(), any());
        verify(goalMapper).toDto(goalEntity);
        verify(goalMapper).toDto(secondGoalEntity);
    }

    @Test
    @DisplayName("Test FindById Positive")
    void testFindGoalByIdPositive() {
        long goalId = 1L;
        Goal goal = Goal.builder()
                .id(1L)
                .build();
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        Goal result = goalService.findGoalById(goalId);

        verify(goalRepository, times(1)).findById(goalId);
        assertNotNull(result);
        assertEquals(goalId, result.getId());
    }

    @Test
    @DisplayName("Test FindById Negative")
    void testFindByIdNegative() {
        long goalId = 1L;
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> goalService.findGoalById(goalId));
        assertEquals(String.format("Goal not found by id: %s", goalId), exception.getMessage());
        verify(goalRepository, times(1)).findById(goalId);
    }
}
