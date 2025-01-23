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
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.exception.GoalAlreadyCompletedException;
import school.faang.user_service.outbox.OutboxEventProcessor;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.OutboxEvent;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.GoalAlreadyCompletedException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalFilter;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.utils.Helper;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private OutboxEventProcessor outboxEventProcessor;

    @Mock
    private GoalCompletedEventPublisher goalCompletedEventPublisher;

    @Mock
    private Helper helper;

    private final long goalId = 1L;
    private final long userId = 1L;
    private GoalDto goalDto;
    private GoalDto secondGoalDto;
    private Goal goalEntity;
    private Goal secondGoalEntity;

    @BeforeEach
    void setUp() {
        goalDto = new GoalDto();
        goalDto.setId(goalId);
        goalDto.setTitle("Test Goal");
        goalDto.setDescription("This is a test goal");
        goalDto.setStatus(GoalStatus.ACTIVE);
        goalDto.setSkillIds(List.of(1L, 2L, 3L));

        secondGoalDto = new GoalDto();
        secondGoalDto.setId(goalId);
        secondGoalDto.setTitle("Test Goal");
        secondGoalDto.setDescription("This is a test goal");
        secondGoalDto.setStatus(GoalStatus.ACTIVE);
        secondGoalDto.setSkillIds(List.of(1L, 2L, 3L));

        goalEntity = new Goal();
        goalEntity.setId(goalDto.getId());
        goalEntity.setTitle(goalDto.getTitle());
        goalEntity.setDescription(goalDto.getDescription());
        goalEntity.setStatus(goalDto.getStatus());

        secondGoalEntity = new Goal();
        secondGoalEntity.setId(goalDto.getId());
        secondGoalEntity.setTitle(goalDto.getTitle() + " 2");
        secondGoalEntity.setDescription(goalDto.getDescription() + " 2");
        secondGoalEntity.setStatus(goalDto.getStatus());
    }

    @Test
    public void testCreateGoalSuccessCreated() {
        when(userService.getUserById(userId)).thenReturn(Optional.of(new User()));
        when(goalMapper.toEntity(goalDto)).thenReturn(goalEntity);
        when(goalRepository.save(goalEntity)).thenReturn(goalEntity);
        when(goalMapper.toDto(goalEntity)).thenReturn(goalDto);

        GoalDto createdGoal = goalService.createGoal(userId, goalDto);

        assertNotNull(createdGoal);
        assertEquals("Test Goal", createdGoal.getTitle());
        assertEquals("This is a test goal", createdGoal.getDescription());
        verify(goalValidation, times(1)).validateGoalRequest(userId, goalDto, true);
        verify(goalRepository, times(1)).save(goalEntity);
    }

    @Test
    void testCreateGoalWithParentGoal() {
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
        Skill firstSkill = new Skill();
        firstSkill.setId(1L);
        Skill secondSkill = new Skill();
        secondSkill.setId(2L);
        goalDto.setSkillIds(List.of(firstSkill.getId(), secondSkill.getId()));

        when(userService.getUserById(userId)).thenReturn(Optional.of(new User()));
        when(goalMapper.toEntity(goalDto)).thenReturn(goalEntity);
        when(skillService.getSkillById(firstSkill.getId())).thenReturn(firstSkill);
        when(skillService.getSkillById(secondSkill.getId())).thenReturn(secondSkill);
        when(goalRepository.save(goalEntity)).thenReturn(goalEntity);
        when(goalMapper.toDto(goalEntity)).thenReturn(goalDto);

        GoalDto createdGoal = goalService.createGoal(userId, goalDto);

        assertNotNull(createdGoal);
        assertEquals(2, goalEntity.getSkillsToAchieve().size());
        verify(skillService, times(1)).getSkillById(firstSkill.getId());
        verify(skillService, times(1)).getSkillById(secondSkill.getId());
        verify(goalRepository).save(goalEntity);
    }

    @Test
    void testCreateGoalValidationFails() {
        doThrow(new DataValidationException("Validation failed")).when(goalValidation).validateGoalRequest(userId, goalDto, true);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalService.createGoal(userId, goalDto));
        assertEquals("Validation failed", exception.getMessage());
        verify(goalValidation).validateGoalRequest(userId, goalDto, true);
        verify(goalRepository, never()).save(any());
    }

    @Test
    void testUpdateGoalSuccessUpdated() {
        when(goalRepository.findById(goalDto.getId())).thenReturn(Optional.ofNullable(goalEntity));
        when(goalMapper.toDto(goalEntity)).thenReturn(goalDto);

        GoalDto updatedGoal = goalService.updateGoal(userId, goalDto);

        assertNotNull(updatedGoal);
        assertEquals("Test Goal", updatedGoal.getTitle());
        assertEquals("This is a test goal", updatedGoal.getDescription());
        verify(goalValidation).validateGoalRequest(userId, goalDto, false);
        verify(goalRepository).save(goalEntity);
    }

    @Test
    void testUpdateGoalWithMentor() {
        Long mentorId = 2L;
        goalDto.setMentorId(mentorId);
        User mentor = new User();
        mentor.setId(mentorId);

        when(goalRepository.findById(goalDto.getId())).thenReturn(Optional.ofNullable(goalEntity));
        when(userService.getUserById(mentorId)).thenReturn(Optional.of(mentor));
        when(goalMapper.toDto(goalEntity)).thenReturn(goalDto);

        GoalDto updatedGoal = goalService.updateGoal(userId, goalDto);

        assertNotNull(updatedGoal);
        assertEquals(mentorId, goalEntity.getMentor().getId());
        verify(userService, times(1)).getUserById(mentorId);
        verify(goalRepository, times(1)).save(goalEntity);
    }

    @Test
    void testUpdateGoalWithParentGoal() {
        Long parentGoalId = 3L;
        goalDto.setParentGoalId(parentGoalId);
        Goal parentGoal = new Goal();
        parentGoal.setId(parentGoalId);

        when(goalRepository.findById(goalDto.getId())).thenReturn(Optional.ofNullable(goalEntity));
        when(goalRepository.findById(parentGoalId)).thenReturn(Optional.of(parentGoal));
        when(goalMapper.toDto(goalEntity)).thenReturn(goalDto);

        GoalDto updatedGoal = goalService.updateGoal(userId, goalDto);

        assertNotNull(updatedGoal);
        assertEquals(parentGoalId, goalEntity.getParent().getId());
        verify(goalRepository).findById(parentGoalId);
        verify(goalRepository).save(goalEntity);
    }

    @Test
    void testNotFoundUpdatedGoal() {
        when(goalRepository.findById(goalDto.getId())).thenReturn(null);

        assertThrows(NullPointerException.class, () -> goalService.updateGoal(userId, goalDto));
        verify(goalRepository, never()).save(any());
    }

    @Test
    void testValidationFailsByUpdateGoal() {
        doThrow(new DataValidationException("Validation failed")).when(goalValidation).validateGoalRequest(userId, goalDto, false);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> goalService.updateGoal(userId, goalDto));
        assertEquals("Validation failed", exception.getMessage());
        verify(goalValidation).validateGoalRequest(userId, goalDto, false);
        verify(goalRepository, never()).save(any());
    }

    @Test
    void testSuccessDeleteGoal() {

        doNothing().when(goalRepository).deleteById(goalId);

        goalService.deleteGoal(goalId);

        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    void testGetGoalsByUserWithFilters() {
        GoalFilterDto filters = new GoalFilterDto();
        GoalFilter mockFilter = mock(GoalFilter.class);

        List<Goal> goals = List.of(goalEntity, secondGoalEntity);

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals);
        when(goalFilters.stream()).thenReturn(Stream.of(mockFilter));
        when(mockFilter.isApplicable(filters)).thenReturn(true);
        when(mockFilter.apply(any(), eq(filters))).thenReturn(goals.stream());
        when(goalMapper.toDto(goalEntity)).thenReturn(goalDto);
        when(goalMapper.toDto(secondGoalEntity)).thenReturn(goalDto);

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
        GoalFilterDto filters = new GoalFilterDto();

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(new ArrayList<>());

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertTrue(result.isEmpty());
        verify(goalRepository).findGoalsByUserId(userId);
        verify(goalFilters).stream();
        verifyNoInteractions(goalMapper);
    }

    @Test
    void testGetGoalsByUserNoApplicableFilters() {
        GoalFilterDto filters = new GoalFilterDto();
        GoalFilter mockFilter = mock(GoalFilter.class);

        List<Goal> goals = List.of(goalEntity, secondGoalEntity);

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals);
        when(goalFilters.stream()).thenReturn(Stream.of(mockFilter));
        when(mockFilter.isApplicable(filters)).thenReturn(false);
        when(goalMapper.toDto(goalEntity)).thenReturn(goalDto);
        when(goalMapper.toDto(secondGoalEntity)).thenReturn(secondGoalDto);

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
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> goalService.findGoalById(goalId));
        assertEquals(String.format("Goal not found by id: %s", goalId), exception.getMessage());
        verify(goalRepository, times(1)).findById(goalId);
    }

    @Test
    void testCompleteTheGoalSuccess() {
        when(goalRepository.findByUserIdAndGoalId(userId, goalId)).thenReturn(Optional.of(goalEntity));
        when(goalRepository.save(goalEntity)).thenReturn(goalEntity);
        goalDto.setStatus(GoalStatus.COMPLETED);
        when(goalMapper.toDto(goalEntity)).thenReturn(goalDto);

        GoalDto actual = goalService.completeTheGoal(userId, goalId);

        verify(goalRepository, times(1)).findByUserIdAndGoalId(userId, goalId);
        verify(goalRepository, times(1)).save(goalEntity);
        verify(outboxEventProcessor, times(1)).saveOutboxEvent(any(OutboxEvent.class));
        assertNotNull(actual);
        assertEquals(goalId, actual.getId());
        assertEquals(GoalStatus.COMPLETED, actual.getStatus());
    }

    @Test
    void testCompleteTheGoalAlreadyCompleted() {
        goalEntity.setStatus(GoalStatus.COMPLETED);
        goalDto.setStatus(GoalStatus.COMPLETED);
        when(goalRepository.findByUserIdAndGoalId(userId, goalId)).thenReturn(Optional.of(goalEntity));
        when(goalMapper.toDto(goalEntity)).thenReturn(goalDto);

        GoalDto actual = goalService.completeTheGoal(userId, goalId);

        verify(goalRepository, times(1)).findByUserIdAndGoalId(userId, goalId);
        verify(goalRepository, never()).save(goalEntity);
        assertNotNull(actual);
        assertEquals(goalId, actual.getId());
        assertEquals(GoalStatus.COMPLETED, actual.getStatus());
    }

    @Test
    void testCompleteTheGoalThrowsEntityNotFoundExceptionIfUserNotFound() {
        when(userService.findUserById(userId)).thenReturn(setUpUserWithoutId());

        assertThrows(EntityNotFoundException.class, () -> goalService.completeTheGoal(userId, goalId));
    }

    @Test
    void testCompleteTheGoalThrowsEntityNotFoundExceptionIfGoalNotFound() {
        when(userService.findUserById(userId)).thenReturn(setUpUserWithoutGoals());

        assertThrows(EntityNotFoundException.class, () -> goalService.completeTheGoal(userId, goalId));
    }

    @Test
    @DisplayName("Test Goal Complete Positive")
    void testGoalCompletePositive() {
        long userId = 4L;
        long goalId = 14L;

        Goal goal = Goal.builder()
                .id(goalId)
                .status(GoalStatus.ACTIVE)
                .build();

        when(goalRepository.findByUserIdAndGoalId(goalId, userId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        goalService.completeGoalAndPublishEvent(goalId, userId);

        verify(goalRepository, times(1)).findByUserIdAndGoalId(goalId, userId);
        verify(goalRepository, times(1)).save(goal);
        verify(goalCompletedEventPublisher, times(1)).publish(any(GoalCompletedEvent.class));
    }

    @Test
    @DisplayName("Test Goal Complete Negative - Goal Already Completed")
    void testGoalCompleteNegativeGoalAlreadyCompleted() {
        long userId = 4L;
        long goalId = 14L;

        Goal goal = Goal.builder()
                .id(goalId)
                .status(GoalStatus.COMPLETED)
                .build();

        when(goalRepository.findByUserIdAndGoalId(goalId, userId)).thenReturn(Optional.of(goal));

        GoalAlreadyCompletedException exception = assertThrows(GoalAlreadyCompletedException.class,
                () -> goalService.completeGoalAndPublishEvent(goalId, userId));

        assertEquals("Goal already completed", exception.getMessage());

        verify(goalRepository, times(1)).findByUserIdAndGoalId(goalId, userId);
        verify(goalRepository, never()).save(any(Goal.class));
        verify(goalCompletedEventPublisher, never()).publish(any(GoalCompletedEvent.class));
    }

    @Test
    @DisplayName("Test Goal Complete Negative - Goal Not Found")
    void testGoalCompleteNegativeGoalNotFound() {
        long userId = 4L;
        long goalId = 14L;

        when(goalRepository.findByUserIdAndGoalId(goalId, userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> goalService.completeGoalAndPublishEvent(goalId, userId));

        assertEquals(String.format("Goal with id %s not found for user %s", goalId, userId), exception.getMessage());

        verify(goalRepository, times(1)).findByUserIdAndGoalId(goalId, userId);
        verify(goalRepository, never()).save(any(Goal.class));
        verify(goalCompletedEventPublisher, never()).publish(any(GoalCompletedEvent.class));
    }



    private User setUpUserWithoutGoals() {
        User user = new User();
        user.setId(userId);
        user.setGoals(Collections.emptyList());
        return user;
    }

    private User setUpUserWithoutId() {
        User user = new User();
        user.setGoals(Collections.emptyList());
        return user;
    }
}
