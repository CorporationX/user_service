package school.faang.user_service.service.goal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @InjectMocks
    private GoalService goalService;
    @Mock
    private GoalRepository goalRepository;
    private GoalFilter filter1;
    private GoalFilter filter2;
    @Mock
    private GoalMapper goalMapper;

    @BeforeEach
    void setUp() {
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
    public void testDeleteGoal_ExistingGoal() {
        long goalId = 1L;

        goalService.deleteGoal(goalId);

        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    public void testDeleteGoal_NonExistentGoal() {
        long nonExistentGoalId = 10L;

        doNothing().when(goalRepository).deleteById(anyLong());

        goalService.deleteGoal(nonExistentGoalId);

        verify(goalRepository, times(1)).deleteById(nonExistentGoalId);
    }

    @Test
    public void getAllGoalsByUserTest() {
        goalService.getGoalsByUser(1L);

        verify(goalRepository, times(1)).findGoalsByUserId(1L);
    }
}