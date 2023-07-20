package school.faang.user_service.service.goal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoalServiceTest {
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;
    private GoalFilter filter1;
    private GoalFilter filter2;
    @Mock
    private GoalMapper goalMapper;
    private List<Goal> mockSubtasks;
    private List<GoalDto> mockDtoList;

    @BeforeEach
    void setUp() {
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
        MockitoAnnotations.initMocks(this);
        goalService = new GoalService(goalRepository, goalMapper, new ArrayList<>());
    }

    @Test
    void testGetGoalsByUser_EmptyGoalFiltersAndEmptyGoalRepository() {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        when(goalRepository.findAll()).thenReturn(new ArrayList<>());

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertEquals(0, result.size());
    }

    @Test
    void testGetGoalsByUser_WithNonApplicableGoalFilter() {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        GoalFilter nonApplicableFilter = mock(GoalFilter.class);
        when(nonApplicableFilter.isApplicable(filters)).thenReturn(false);

        List<GoalFilter> goalFilters = List.of(nonApplicableFilter);
        goalService = new GoalService(goalRepository, goalMapper, goalFilters);

        List<Goal> goals = List.of(new Goal(), new Goal());
        when(goalRepository.findAll()).thenReturn(goals);

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertEquals(0, result.size());
    }

    @Test
    void testGetGoalsByUser_WithApplicableGoalFilter() {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        GoalFilter applicableFilter = mock(GoalFilter.class);
        when(applicableFilter.isApplicable(filters)).thenReturn(true);

        List<GoalFilter> goalFilters = List.of(applicableFilter);
        goalService = new GoalService(goalRepository, goalMapper, goalFilters);

        List<Goal> goals = List.of(new Goal(), new Goal());
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
    public void testFindSubtasksByGoalId_ValidGoalIdNoFilters_ReturnsAllSubtasks() {
        long goalId = 1;
        GoalFilterDto filter = null;

        when(goalRepository.findByParent(goalId)).thenReturn(mockSubtasks.stream());
        when(filter1.isApplicable(filter)).thenReturn(false);
        when(filter2.isApplicable(filter)).thenReturn(false);
        when(goalMapper.toDto(any())).thenReturn(mockDtoList.get(0), mockDtoList.get(1));

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertEquals(mockDtoList, result);
    }

    @Test
    public void testFindSubtasksByGoalId_ValidGoalIdWithFilters_ReturnsFilteredSubtasks() {
        long goalId = 1;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setSkillIds(List.of(1L));

        when(goalRepository.findByParent(goalId)).thenReturn(mockSubtasks.stream());
        when(filter1.isApplicable(filter)).thenReturn(true);
        when(filter2.isApplicable(filter)).thenReturn(false);
        when(filter1.applyFilter(any(), any())).thenReturn(mockSubtasks.stream());
        when(goalMapper.toDto(any())).thenReturn(mockDtoList.get(0), mockDtoList.get(1));

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertEquals(mockDtoList, result);
    }

    @Test
    public void testFindSubtasksByGoalId_InvalidGoalId() {
        long invalidGoalId = 999;
        GoalFilterDto filter = null;

        when(goalRepository.findByParent(invalidGoalId)).thenReturn(Stream.empty());

        List<GoalDto> result = goalService.findSubtasksByGoalId(invalidGoalId, filter);

        assertEquals(0, result.size());
    }

    @Test
    public void testFindSubtasksByGoalId_ValidGoalIdWithApplicableFilters_ReturnsFilteredSubtasks() {
        long goalId = 1;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setSkillIds(List.of(1L));

        when(goalRepository.findByParent(goalId)).thenReturn(mockSubtasks.stream());
        when(filter1.isApplicable(filter)).thenReturn(true);
        when(filter2.isApplicable(filter)).thenReturn(true);
        when(filter1.applyFilter(any(), any())).thenReturn(mockSubtasks.stream());
        when(filter2.applyFilter(any(), any())).thenReturn(mockSubtasks.stream());
        when(goalMapper.toDto(any())).thenReturn(mockDtoList.get(0), mockDtoList.get(1));

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertEquals(mockDtoList, result);
    }

    @Test
    public void testFindSubtasksByGoalId_NullFilter_ReturnsAllSubtasks() {
        long goalId = 1;
        GoalFilterDto filter = null;

        when(goalRepository.findByParent(goalId)).thenReturn(mockSubtasks.stream());
        when(filter1.isApplicable(filter)).thenReturn(false);
    }
}