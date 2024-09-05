package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.publisher.goal.GoalEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class FilterTest {
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private GoalFilter goalFilter;

    @Mock
    private GoalEventPublisher goalEventPublisher;

    @InjectMocks
    private GoalService goalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Инициализируем goalFilters и передаем в конструктор GoalService
        List<GoalFilter> goalFilters = List.of(goalFilter);
        goalService = new GoalService(goalRepository, null, null, goalMapper, goalFilters, goalEventPublisher);
    }

    private Goal createGoal(Long id, String title, String description, GoalStatus status) {
        Goal goal = new Goal();
        goal.setId(id);
        goal.setTitle(title);
        goal.setDescription(description);
        goal.setStatus(status);
        return goal;
    }

    private GoalDto createGoalDto(Long id, String title, String description, String status) {
        GoalDto goalDto = new GoalDto();
        goalDto.setId(id);
        goalDto.setTitle(title);
        goalDto.setDescription(description);
        goalDto.setStatus(status);
        return goalDto;
    }

    private List<Goal> createGoals() {
        return List.of(
                createGoal(1L, "Test Goal", "Description", GoalStatus.ACTIVE),
                createGoal(2L, "Another Goal", "Description", GoalStatus.COMPLETED),
                createGoal(3L, "Test Goal 2", "Description", GoalStatus.ACTIVE)
        );
    }

    private void setupMocksForFindSubtasksByGoalId(List<Goal> goals, GoalFilterDto filter, List<Goal> filteredGoals, List<GoalDto> goalDtos) {
        long goalId = 1L;
        Stream<Goal> goalStream = goals.stream();
        Stream<Goal> filteredGoalStream = filteredGoals.stream();

        when(goalRepository.findByParent(goalId)).thenReturn(goalStream);
        when(goalFilter.isApplicable(filter)).thenReturn(true);
        when(goalFilter.apply(goalStream, filter)).thenReturn(filteredGoalStream);
        for (int i = 0; i < filteredGoals.size(); i++) {
            when(goalMapper.toDto(filteredGoals.get(i))).thenReturn(goalDtos.get(i));
        }
    }

    @Test
    void findSubtasksByGoalIdFilterByStatusTest() {
        long goalId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setStatus(GoalStatus.ACTIVE);

        List<Goal> goals = createGoals();
        List<Goal> filteredGoals = List.of(goals.get(0), goals.get(2));
        List<GoalDto> goalDtos = List.of(
                createGoalDto(1L, "Test Goal", "Description", "active"),
                createGoalDto(3L, "Test Goal 2", "Description", "active")
        );

        setupMocksForFindSubtasksByGoalId(goals, filter, filteredGoals, goalDtos);

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertEquals(2, result.size());
        assertEquals(goalDtos.get(0).getId(), result.get(0).getId());
        assertEquals(goalDtos.get(1).getId(), result.get(1).getId());
    }

    @Test
    void findSubtasksByGoalIdFilterByTitleTest() {
        long goalId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle("Another");

        Goal goal1 = createGoal(1L, "Test Goal", "Description", GoalStatus.ACTIVE);
        Goal goal2 = createGoal(2L, "Another Goal", "Description", GoalStatus.COMPLETED);
        Stream<Goal> stream = Stream.of(goal1, goal2);

        when(goalRepository.findByParent(goalId)).thenReturn(stream);
        when(goalFilter.isApplicable(filter)).thenReturn(false);
        when(goalFilter.apply(stream, filter)).thenReturn(Stream.of());

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertEquals(0, result.size());
    }

    @Test
    void findSubtasksByGoalIdFilterByNullTest() {
        long goalId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle(null);

        Goal goal1 = createGoal(1L, "Test Goal", "Description", GoalStatus.ACTIVE);
        Goal goal2 = createGoal(2L, "Another Goal", "Description", GoalStatus.COMPLETED);
        Stream<Goal> stream = Stream.of(goal1, goal2);

        GoalDto goalDto1 = createGoalDto(2L, "Another Goal", "Description", "completed");

        when(goalRepository.findByParent(goalId)).thenReturn(stream);
        when(goalFilter.isApplicable(filter)).thenReturn(true);
        when(goalFilter.apply(stream, filter)).thenReturn(Stream.of(goal2));
        when(goalMapper.toDto(goal2)).thenReturn(goalDto1);

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertEquals(1, result.size());
        assertEquals(goalDto1.getId(), result.get(0).getId());
        assertEquals(goalDto1.getTitle(), result.get(0).getTitle());
        assertEquals(goalDto1.getStatus(), result.get(0).getStatus());
    }

    @Test
    public void getGoalsByUserWithFilterByStatusTest() {
        long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setStatus(GoalStatus.ACTIVE);

        List<Goal> goals = createGoals();
        Stream<Goal> stream = goals.stream();
        List<Goal> filteredGoals = List.of(goals.get(0));
        Stream<Goal> resultStream = filteredGoals.stream();

        GoalDto goalDto1 = createGoalDto(1L, "Test Goal", "Description", "active");

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(stream);
        when(goalFilter.isApplicable(filter)).thenReturn(true);
        when(goalFilter.apply(stream, filter)).thenReturn(resultStream);
        when(goalMapper.toDto(filteredGoals.get(0))).thenReturn(goalDto1);

        List<GoalDto> result = goalService.getGoalsByUser(userId, filter);

        assertEquals(1, result.size());
        assertEquals(goalDto1.getId(), result.get(0).getId());
        assertEquals(goalDto1.getTitle(), result.get(0).getTitle());
        assertEquals(goalDto1.getStatus(), result.get(0).getStatus());

        verify(goalRepository, times(1)).findGoalsByUserId(userId);
    }

    @Test
    public void getGoalsByUserWithFilterByTitleTest() {
        long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle("Another");

        List<Goal> goals = createGoals();
        Stream<Goal> stream = goals.stream();

        GoalDto goalDto1 = createGoalDto(2L, "Another Goal", "Description", "completed");

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(stream);
        when(goalFilter.isApplicable(filter)).thenReturn(true);
        when(goalFilter.apply(stream, filter)).thenReturn(Stream.of(goals.get(1)));
        when(goalMapper.toDto(goals.get(1))).thenReturn(goalDto1);

        List<GoalDto> result = goalService.getGoalsByUser(userId, filter);

        assertEquals(1, result.size());
        assertEquals(goalDto1.getId(), result.get(0).getId());
        assertEquals(goalDto1.getTitle(), result.get(0).getTitle());
        assertEquals(goalDto1.getStatus(), result.get(0).getStatus());

        verify(goalRepository, times(1)).findGoalsByUserId(userId);
    }

    @Test
    public void getGoalsByUserWithFilterByNullTest() {
        long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle(null);

        List<Goal> goals = createGoals();
        Stream<Goal> stream = goals.stream();

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(stream);
        when(goalFilter.isApplicable(filter)).thenReturn(false);
        when(goalFilter.apply(stream, filter)).thenReturn(Stream.of());

        List<GoalDto> result = goalService.getGoalsByUser(userId, filter);

        assertEquals(0, result.size());

        verify(goalRepository, times(1)).findGoalsByUserId(userId);
    }
}