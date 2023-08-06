package school.faang.user_service.service.goal;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.filter.GoalFilter;
import school.faang.user_service.dto.goal.filter.GoalFilterDto;
import school.faang.user_service.dto.goal.filter.GoalStatusFilter;
import school.faang.user_service.dto.goal.filter.GoalTitleFilter;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class GoalServiceTest {
    private GoalService goalService;
    private GoalRepository goalRepository;

    @BeforeEach
    public void setUp() {
        goalRepository = mock(GoalRepository.class);
        GoalMapper mapper = mock(GoalMapper.class);
        GoalFilter titleFilter = new GoalTitleFilter();
        GoalFilter statusFilter = new GoalStatusFilter();
        List<GoalFilter> filters = List.of(titleFilter, statusFilter);
        goalService = new GoalService(goalRepository, mapper, filters);
    }

    @Test
    void testCreateGoal() {
        Goal parentGoal = new Goal();
        Goal goal = Goal.builder().title("smth").parent(parentGoal).build();
        goalService.createGoal(1L, goal);
        verify(goalRepository, times(1)).create(any(), any(), any());
    }

    @Test
    void testCreateGoalWithoutTitle() {
        Goal goal = new Goal();
        assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(1L, goal));
    }

    @Test
    void testCreateGoalOverloaded() {
        Goal parentGoal = new Goal();
        Goal goal1 = Goal.builder()
                .status(GoalStatus.ACTIVE)
                .title("1")
                .parent(parentGoal)
                .build();
        Goal goal2 = Goal.builder()
                .status(GoalStatus.ACTIVE)
                .title("2")
                .parent(parentGoal)
                .build();
        Goal goal3 = Goal.builder()
                .status(GoalStatus.ACTIVE)
                .title("3")
                .parent(parentGoal)
                .build();
        goalService.createGoal(1L, goal1);
        goalService.createGoal(1L, goal2);
        goalService.createGoal(1L, goal3);
        Goal goal = Goal.builder().parent(parentGoal).build();
        assertThrows(IllegalArgumentException.class,
                () -> goalService.createGoal(1L, goal));
    }

    @Test
    public void testGetGoalsByUserTitleFilter() {
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("1")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L).title("1")
                .status(GoalStatus.COMPLETED)
                .build();
        Goal goal3 = Goal.builder()
                .id(3L)
                .title("2")
                .status(GoalStatus.ACTIVE)
                .build();
        Long userId = 1L;
        when(goalService.findGoalsByUserId(userId)).thenReturn(Stream.of(goal1, goal2, goal3));
        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setTitleFilter("1");
        List<GoalDto> filteredGoals = goalService.getGoalsByUser(userId, filterDto);
        assertEquals(2, filteredGoals.size());
    }

    @Test
    public void testGetGoalsByUserStatusFilter() {
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("1")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("2")
                .status(GoalStatus.COMPLETED)
                .build();
        Goal goal3 = Goal.builder()
                .id(3L)
                .title("3")
                .status(GoalStatus.ACTIVE)
                .build();
        Long userId = 1L;
        when(goalService.findGoalsByUserId(userId)).thenReturn(Stream.of(goal1, goal2, goal3));
        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setStatusFilter(GoalStatus.ACTIVE);
        List<GoalDto> filteredGoals = goalService.getGoalsByUser(userId, filterDto);
        assertEquals(2, filteredGoals.size());
    }

    @Test
    public void testGetGoalsByUserBothFilter() {
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("1")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("2")
                .status(GoalStatus.COMPLETED)
                .build();
        Goal goal3 = Goal.builder()
                .id(3L)
                .title("3")
                .status(GoalStatus.ACTIVE)
                .build();
        Long userId = 1L;
        when(goalService.findGoalsByUserId(userId)).thenReturn(Stream.of(goal1, goal2, goal3));
        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setTitleFilter("1");
        filterDto.setStatusFilter(GoalStatus.ACTIVE);
        List<GoalDto> filteredGoals = goalService.getGoalsByUser(userId, filterDto);
        assertEquals(1, filteredGoals.size());
    }

    @Test
    public void testGetGoalsByUserNoFilter() {
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("1")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L).title("2")
                .status(GoalStatus.COMPLETED)
                .build();
        Goal goal3 = Goal.builder()
                .id(3L)
                .title("3")
                .status(GoalStatus.ACTIVE)
                .build();
        Long userId = 1L;
        when(goalService.findGoalsByUserId(userId)).thenReturn(Stream.of(goal1, goal2, goal3));
        GoalFilterDto filterDto = new GoalFilterDto();
        List<GoalDto> filteredGoals = goalService.getGoalsByUser(userId, filterDto);
        assertEquals(3, filteredGoals.size());
    }

    @Test
    public void testFindSubtasksByGoalIdTitleFilter() {
        Goal parentGoal = Goal.builder()
                .id(10L)
                .title("parent")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("1")
                .status(GoalStatus.ACTIVE)
                .parent(parentGoal)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("1")
                .status(GoalStatus.COMPLETED)
                .parent(parentGoal)
                .build();
        Goal goal3 = Goal.builder()
                .id(3L)
                .title("2")
                .status(GoalStatus.ACTIVE)
                .parent(parentGoal)
                .build();
        Long goalId = parentGoal.getId();
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(goal1, goal2, goal3));
        GoalFilterDto goalFilterDto = new GoalFilterDto();
        goalFilterDto.setTitleFilter("1");
        List<GoalDto> filteredSubtasks = goalService.getSubtasksByGoalId(goalId, goalFilterDto);
        assertEquals(2, filteredSubtasks.size());
    }

    @Test
    public void testGetSubtasksByGoalIdStatusFilter() {
        Goal parentGoal = Goal.builder()
                .id(10L)
                .title("parent")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("1")
                .status(GoalStatus.ACTIVE)
                .parent(parentGoal)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("2")
                .status(GoalStatus.COMPLETED)
                .parent(parentGoal)
                .build();
        Goal goal3 = Goal.builder()
                .id(3L)
                .title("3")
                .status(GoalStatus.ACTIVE)
                .parent(parentGoal)
                .build();
        Long goalId = parentGoal.getId();
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(goal1, goal2, goal3));
        GoalFilterDto goalFilterDto = new GoalFilterDto();
        goalFilterDto.setStatusFilter(GoalStatus.ACTIVE);
        List<GoalDto> filteredSubtasks = goalService.getSubtasksByGoalId(goalId, goalFilterDto);
        assertEquals(2, filteredSubtasks.size());
    }

    @Test
    public void testGetSubtasksByGoalIdBothFilter() {
        Goal parentGoal = Goal.builder()
                .id(10L)
                .title("parent")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("1")
                .status(GoalStatus.ACTIVE)
                .parent(parentGoal)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("2")
                .status(GoalStatus.COMPLETED)
                .parent(parentGoal)
                .build();
        Goal goal3 = Goal.builder()
                .id(3L)
                .title("2")
                .status(GoalStatus.ACTIVE)
                .parent(parentGoal)
                .build();
        Long goalId = parentGoal.getId();
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(goal1, goal2, goal3));
        GoalFilterDto goalFilterDto = new GoalFilterDto();
        goalFilterDto.setTitleFilter("2");
        goalFilterDto.setStatusFilter(GoalStatus.ACTIVE);
        List<GoalDto> filteredSubtasks = goalService.getSubtasksByGoalId(goalId, goalFilterDto);
        assertEquals(1, filteredSubtasks.size());
    }

    @Test
    public void testGetSubtasksByGoalIdNoFilter() {
        Goal parentGoal = Goal.builder()
                .id(10L)
                .title("parent")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("1")
                .status(GoalStatus.ACTIVE)
                .parent(parentGoal)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("1")
                .status(GoalStatus.COMPLETED)
                .parent(parentGoal)
                .build();
        Goal goal3 = Goal.builder()
                .id(3L)
                .title("2")
                .status(GoalStatus.ACTIVE)
                .parent(parentGoal)
                .build();
        Long goalId = parentGoal.getId();
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(goal1, goal2, goal3));
        GoalFilterDto goalFilterDto = new GoalFilterDto();
        List<GoalDto> filteredSubtasks = goalService.getSubtasksByGoalId(goalId, goalFilterDto);
        assertEquals(3, filteredSubtasks.size());
    }

    @Test
    void testDeleteGoal() {
        when(goalRepository.existsById(1L)).thenReturn(true);
        goalService.deleteGoal(1L);
        verify(goalRepository, times(1)).deleteById(any());
    }

    @Test
    void testDeleteGoalNotExisted() {
        when(goalRepository.existsById(1L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class,
                () -> goalService.deleteGoal(1L));
    }

}