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
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.User;
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
    void testGetGoalsByUserWithoutFilters() {
        goalService.getGoalsByUser(1L);

        verify(goalRepository, times(1)).findGoalsByUserId(1L);
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
    public void testGetAllGoalsByUser() {
        goalService.getGoalsByUser(1L);

        verify(goalRepository, times(1)).findGoalsByUserId(1L);
    }

    @Test
    @DisplayName("Should return goal by id")
    public void testGetById() {
        Goal running = new Goal();
        running.setId(1L);
        running.setTitle("Running");
        running.setDescription("Running description");

        when(goalRepository.findById(1L)).thenReturn(Optional.of(running));
        goalService.get(1L);
        Mockito.verify(goalMapper, Mockito.times(1)).toDto(running);
    }

    @Test
    @DisplayName("Should update goal successfully")
    public void testUpdateGoal() {
        Goal running = new Goal();
        running.setId(1L);
        running.setTitle("Running");
        running.setDescription("Running description");

        when(goalRepository.findById(1L)).thenReturn(Optional.of(running));

        GoalDto goalToUpdate = new GoalDto();
        goalToUpdate.setId(1L);
        goalToUpdate.setTitle("Update goal");
        goalToUpdate.setDescription("Update goal");

        goalService.update(goalToUpdate);
        Mockito.verify(goalMapper, Mockito.times(1)).update(goalMapper.toDto(running), goalToUpdate);
        Mockito.verify(goalRepository, Mockito.times(1)).save(goalMapper.toEntity(goalMapper.toDto(running)));
    }

    @Test
    @DisplayName("Should remove specific user from goal.users list, if users > 2")
    public void removeUserFromGoalsTest() {
        Goal running = new Goal();
        Goal swimming = new Goal();
        Goal coding = new Goal();
        Goal relaxing = new Goal();

        User alex = new User();
        alex.setId(1L);

        User blake = new User();
        blake.setId(2L);

        running.setId(1L);
        running.setUsers(List.of(alex, blake));

        swimming.setId(2L);
        swimming.setUsers(List.of(alex, blake));

        coding.setId(3L);
        coding.setUsers(List.of(alex, blake));

        relaxing.setId(4L);
        relaxing.setUsers(List.of(alex, blake));

        when(goalRepository.findAllById(List.of(1L, 2L, 3L, 4L))).thenReturn(List.of(running, swimming,coding, relaxing));

        int removedUsersFromGoalCount = goalService.removeUserFromGoals(List.of(1L, 2L, 3L, 4L), 1L);

        assertEquals(4, removedUsersFromGoalCount);
        assertEquals(1, running.getUsers().size());
        assertEquals(1, swimming.getUsers().size());
        assertEquals(1, coding.getUsers().size());
        assertEquals(1, relaxing.getUsers().size());
    }

    @Test
    @DisplayName("Should call goalRepository.deleteAllById")
    void testDeleteAllByIds() {
        goalService.deleteAllByIds(List.of(1L, 2L, 3L, 4L));

        verify(goalRepository, times(1)).deleteAllById(List.of(1L, 2L, 3L, 4L));
    }
}