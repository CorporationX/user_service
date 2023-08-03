package school.faang.user_service.service.goal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.GoalValidator;
import school.faang.user_service.entity.User;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GoalServiceTest {
    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Spy
    private GoalMapper goalMapper;

    private List<GoalFilter> goalFilters;

    @Mock
    private GoalValidator goalValidator;
    private List<Goal> mockSubtasks;
    private List<GoalDto> mockDtoList;
    private GoalFilter filter1;
    private GoalFilter filter2;
    private Long id;

    @BeforeEach
    public void setUp() {
        id = 1L;
        mockSubtasks = new ArrayList<>();
        Goal subtask1 = new Goal();
        subtask1.setId(1L);
        subtask1.setTitle("Subtask 1");
        Goal subtask2 = new Goal();
        subtask2.setId(2L);
        subtask2.setTitle("Subtask 2");
        mockSubtasks.add(subtask1);
        mockSubtasks.add(subtask2);

        mockDtoList = new ArrayList<>();
        GoalDto dto1 = new GoalDto();
        dto1.setId(1L);
        dto1.setTitle("Subtask 1");
        GoalDto dto2 = new GoalDto();
        dto2.setId(2L);
        dto2.setTitle("Subtask 2");
        mockDtoList.add(dto1);
        mockDtoList.add(dto2);

        filter1 = mock(GoalFilter.class);
        filter2 = mock(GoalFilter.class);

        goalService = new GoalService(goalRepository, goalMapper, goalFilters, goalValidator);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test getGoalsByUser with applicable filters")
    public void testGetGoalsByUser_WithApplicableFilters() {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        List<Goal> mockGoals = new ArrayList<>();
        mockGoals.add(new Goal());
        mockGoals.add(new Goal());
        Stream<Goal> goalsStream = mockGoals.stream();
        when(goalRepository.findAll()).thenReturn(mockGoals);

        List<Goal> filteredGoals = new ArrayList<>();
        filteredGoals.add(new Goal());

        when(filter1.isApplicable(filters)).thenReturn(true);
        when(filter2.isApplicable(filters)).thenReturn(true);
        when(filter1.applyFilter(goalsStream, filters)).thenReturn(filteredGoals.stream());
        when(filter2.applyFilter(goalsStream, filters)).thenReturn(filteredGoals.stream());

        GoalDto goalDto = new GoalDto();
        when(goalMapper.toDto(any(Goal.class))).thenReturn(goalDto);

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(goalDto, result.get(0));
        assertEquals(goalDto, result.get(1));

        verify(filter1).isApplicable(filters);
        verify(filter2).isApplicable(filters);
        verify(filter1).applyFilter(goalsStream, filters);
        verify(filter2).applyFilter(goalsStream, filters);
        verify(goalMapper, times(2)).toDto(any(Goal.class));
    }

    @Test
    @DisplayName("Test getGoalsByUser with no applicable filters")
    void testGetGoalsByUser_WithNonApplicableGoalFilter() {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();
        GoalFilter nonApplicableFilter = mock(GoalFilter.class);
        when(nonApplicableFilter.isApplicable(filters)).thenReturn(false);

        List<GoalFilter> goalFilters = List.of(nonApplicableFilter);

        goalService = new GoalService(goalRepository, goalMapper, goalFilters, goalValidator);

        List<Goal> goals = List.of(new Goal(), new Goal());
        when(goalRepository.findAll()).thenReturn(goals);
        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test getGoalsByUser with no filters")
    public void testGetGoalsByUser_NoFilters() {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        List<Goal> mockGoals = new ArrayList<>();
        when(goalRepository.findAll()).thenReturn(mockGoals);

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertNotNull(result);
        assertEquals(0, result.size());
    }


    @Test
    @DisplayName("Test createGoal")
    public void testCreateGoal() {
        Long userId = 1L;
        GoalDto mockGoalDto = new GoalDto();

        Goal mockGoal = mock(Goal.class);

        when(mockGoal.getId()).thenReturn(1L);
        when(mockGoal.getParent()).thenReturn(mock(Goal.class));
        when(mockGoal.getParent().getId()).thenReturn(2L);

        when(goalMapper.toEntity(any(GoalDto.class))).thenReturn(mockGoal);
        doNothing().when(goalValidator).validateGoal(anyLong(), any(GoalDto.class));
        when(goalRepository.save(any(Goal.class))).thenReturn(mockGoal);
        when(goalMapper.toDto(any(Goal.class))).thenReturn(mockGoalDto);

        GoalDto result = goalService.createGoal(userId, mockGoalDto);

        assertNotNull(result);
        assertEquals(mockGoalDto, result);
    }

    @Test
    @DisplayName("test getGoalsByUser with applicable goalFilter")
    void testGetGoalsByUser_WithApplicableGoalFilter() {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        GoalFilter applicableFilter = mock(GoalFilter.class);
        when(applicableFilter.isApplicable(filters)).thenReturn(true);

        List<GoalFilter> goalFilters = List.of(applicableFilter);
        goalService = new GoalService(goalRepository, goalMapper, goalFilters, goalValidator);

        List<Goal> goals = Arrays.asList(new Goal(), new Goal());
        when(goalRepository.findAll()).thenReturn(goals);

        GoalDto goalDto1 = new GoalDto();
        GoalDto goalDto2 = new GoalDto();
        when(goalMapper.toDto(any(Goal.class))).thenReturn(goalDto1, goalDto2);

        Stream<Goal> filteredGoals = goals.stream();
        when(applicableFilter.applyFilter(any(Stream.class), eq(filters))).thenReturn(filteredGoals);

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertEquals(2, result.size());
        assertEquals(goalDto1, result.get(0));
        assertEquals(goalDto2, result.get(1));
        verify(goalMapper, times(2)).toDto(any(Goal.class));
    }

    @Test
    @DisplayName("test updateGoal with valid goal")
    public void testUpdateGoal_ValidGoal() {
        GoalDto goalDto = new GoalDto();
        goalDto.setStatus(GoalStatus.ACTIVE);
        goalDto.setId(1L);

        Goal existingGoal = new Goal();
        existingGoal.setStatus(GoalStatus.ACTIVE);
        existingGoal.setId(1L);

        Goal updatedGoal = new Goal();
        updatedGoal.setStatus(GoalStatus.ACTIVE);
        updatedGoal.setId(1L);

        when(goalRepository.findById(eq(1L))).thenReturn(Optional.of(existingGoal));
        when(goalMapper.updateFromDto(goalDto, existingGoal)).thenReturn(updatedGoal);
        when(goalRepository.save(any(Goal.class))).thenReturn(updatedGoal);
        when(goalMapper.toDto(updatedGoal)).thenReturn(goalDto);

        long goalId = 1L;
        GoalDto result = goalService.updateGoal(goalId, goalDto);

        assertEquals(goalDto, result);
    }

    @Test
    @DisplayName("test updateGoal with nonexistent goal")
    public void testUpdateGoal_NonExistentGoal() {
        when(goalRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        long nonExistentGoalId = 10L;
        GoalDto goalDto = new GoalDto();
        assertThrows(IllegalArgumentException.class, () -> goalService.updateGoal(nonExistentGoalId, goalDto));
    }

    @Test
    @DisplayName("Test findSubtasksByGoalId with no filters")
    public void testFindSubtasksByGoalId_NoFilters() {
        long goalId = 1L;
        GoalFilterDto filter = null;

        List<Goal> mockSubtasks = new ArrayList<>();
        mockSubtasks.add(new Goal());
        mockSubtasks.add(new Goal());
        Stream<Goal> subtasksStream = mockSubtasks.stream();
        when(goalRepository.findByParent(goalId)).thenReturn(subtasksStream);

        GoalDto goalDto = new GoalDto();
        when(goalMapper.toDto(any(Goal.class))).thenReturn(goalDto);

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(goalDto, result.get(0));
        assertEquals(goalDto, result.get(1));

        verifyNoInteractions(filter1, filter2);
        verify(goalMapper, times(2)).toDto(any(Goal.class));
    }

    @Test
    @DisplayName("Test findSubtasksByGoalId with applicable filters")
    public void testFindSubtasksByGoalId_WithApplicableFilters() {
        long goalId = 1L;
        GoalFilterDto filter = new GoalFilterDto();

        List<Goal> mockSubtasks = new ArrayList<>();
        mockSubtasks.add(new Goal());
        Stream<Goal> subtasksStream = mockSubtasks.stream();
        when(goalRepository.findByParent(goalId)).thenReturn(subtasksStream);

        List<Goal> filteredSubtasks = new ArrayList<>();
        filteredSubtasks.add(new Goal());

        when(filter1.isApplicable(filter)).thenReturn(true);
        when(filter2.isApplicable(filter)).thenReturn(true);
        when(filter1.applyFilter(subtasksStream, filter)).thenReturn(filteredSubtasks.stream());
        when(filter2.applyFilter(subtasksStream, filter)).thenReturn(filteredSubtasks.stream());

        GoalDto goalDto = new GoalDto();
        when(goalMapper.toDto(any(Goal.class))).thenReturn(goalDto);

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(goalDto, result.get(0));

        verify(filter1).isApplicable(filter);
        verify(filter2).isApplicable(filter);
        verify(filter1).applyFilter(subtasksStream, filter);
        verify(filter2).applyFilter(subtasksStream, filter);
        verify(goalMapper).toDto(any(Goal.class));
    }

    @Test
    @DisplayName("Test findSubtasksByGoalId with no applicable filters")
    public void testFindSubtasksByGoalId_NoApplicableFilters() {
        long goalId = 1L;
        GoalFilterDto filter = new GoalFilterDto();

        List<Goal> mockSubtasks = new ArrayList<>();
        mockSubtasks.add(new Goal());
        Stream<Goal> subtasksStream = mockSubtasks.stream();
        when(goalRepository.findByParent(goalId)).thenReturn(subtasksStream);

        when(filter1.isApplicable(filter)).thenReturn(false);
        when(filter2.isApplicable(filter)).thenReturn(false);

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(filter1).isApplicable(filter);
        verify(filter2).isApplicable(filter);
        verifyNoInteractions(filter1, filter2);
        verify(goalMapper).toDto(any(Goal.class));
    }

    @Test
    @DisplayName("Test getGoalsByUser")
    public void testGetGoalsByUser() {
        Long userId = 1L;

        List<Goal> mockGoals = new ArrayList<>();
        mockGoals.add(new Goal());
        mockGoals.add(new Goal());
        Stream<Goal> goalsStream = mockGoals.stream();
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goalsStream);

        GoalDto goalDto = new GoalDto();
        when(goalMapper.toDto(any(Goal.class))).thenReturn(goalDto);

        List<GoalDto> result = goalService.getGoalsByUser(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(goalDto, result.get(0));
        assertEquals(goalDto, result.get(1));

        verify(goalMapper, times(2)).toDto(any(Goal.class));
    }

    @Test
    @DisplayName("Test deleteGoal")
    public void testDeleteGoal() {
        long goalId = 1L;

        goalService.deleteGoal(goalId);

        verify(goalRepository).deleteById(goalId);
    }

    @Test
    @DisplayName("Test deleteAllByIds")
    public void testDeleteAllByIds() {
        List<Long> ids = List.of(1L, 2L, 3L);

        goalService.deleteAllByIds(ids);

        verify(goalRepository).deleteAllById(ids);
    }

    @Test
    @DisplayName("Test get")
    public void testGet() {
        Long id = 1L;
        Goal goal = new Goal();
        when(goalRepository.findById(id)).thenReturn(Optional.of(goal));

        GoalDto goalDto = new GoalDto();
        when(goalMapper.toDto(goal)).thenReturn(goalDto);

        GoalDto result = goalService.get(id);

        assertNotNull(result);
        assertEquals(goalDto, result);
    }

    @Test
    @DisplayName("Test update")
    public void testUpdate() {
        GoalDto existingGoalDto = new GoalDto();
        existingGoalDto.setId(1L);

        GoalDto updatedGoalDto = new GoalDto();
        updatedGoalDto.setId(1L);

        Goal existingGoal = new Goal();
        when(goalRepository.findById(existingGoalDto.getId())).thenReturn(Optional.of(existingGoal));

        when(goalMapper.toEntity(existingGoalDto)).thenReturn(existingGoal);
        when(goalRepository.save(existingGoal)).thenReturn(existingGoal);

        GoalDto result = goalService.update(updatedGoalDto);

        assertNotNull(result);
        assertEquals(existingGoalDto, result);

        verify(goalMapper).updateFromDto(existingGoalDto, existingGoal);
        verify(goalRepository).save(existingGoal);
    }

    @Test
    @DisplayName("Test removeUserFromGoals")
    public void testRemoveUserFromGoals() {
        Long userId = 1L;
        List<Long> goalIds = List.of(1L, 2L);

        List<Goal> mockGoals = new ArrayList<>();
        Goal goal1 = new Goal();
        User user = new User();
        user.setId(userId);
        goal1.setUsers(List.of(user));
        mockGoals.add(goal1);

        Goal goal2 = new Goal();
        User user2 = new User();
        user.setId(2L);
        goal2.setUsers(List.of(user2));
        mockGoals.add(goal2);

        when(goalRepository.findAllById(goalIds)).thenReturn(mockGoals);

        int result = goalService.removeUserFromGoals(goalIds, userId);

        assertEquals(1, result);
        assertEquals(0, goal1.getUsers().size());
        assertEquals(1, goal2.getUsers().size());
    }

}