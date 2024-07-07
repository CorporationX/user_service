package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.filter.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


public class FilterTest {
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private GoalFilter goalFilter;

    @InjectMocks
    private GoalService goalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Инициализируем goalFilters и передаем в конструктор GoalService
        List<GoalFilter> goalFilters = List.of(goalFilter);
        goalService = new GoalService(goalRepository, null, null, goalMapper, goalFilters);
    }

    // test for findSubtasksByGoalId

    @Test
    void findSubtasksByGoalId_FilterByStatus() {
        long goalId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setStatus(GoalStatus.ACTIVE);

        Goal goal1 = new Goal();
        goal1.setStatus(GoalStatus.ACTIVE);
        goal1.setId(1L);

        Goal goal2 = new Goal();
        goal2.setStatus(GoalStatus.COMPLETED);
        goal2.setId(2L);

        Goal goal3 = new Goal();
        goal3.setStatus(GoalStatus.ACTIVE);
        goal3.setId(3L);

        List<Goal> list = List.of(goal1, goal2, goal3);
        Stream<Goal> goalStream = list.stream();
        List<Goal> sortedList = List.of(goal1, goal3);
        Stream<Goal> sortedGoalStream = sortedList.stream();

        GoalDto goalDto1 = new GoalDto();
        goalDto1.setId(1L);
        goalDto1.setStatus("active");

        GoalDto goalDto3 = new GoalDto();
        goalDto3.setId(3L);
        goalDto3.setStatus("active");

        when(goalRepository.findByParent(goalId)).thenReturn(goalStream);
        when(goalFilter.isApplicable(filter)).thenReturn(true);
        when(goalFilter.apply(goalStream, filter)).thenReturn(sortedGoalStream);
        when(goalMapper.toDto(goal1)).thenReturn(goalDto1);
        when(goalMapper.toDto(goal3)).thenReturn(goalDto3);

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertEquals(2, result.size());
        assertEquals(goalDto1.getId(), result.get(0).getId());
        assertEquals(goalDto3.getId(), result.get(1).getId());
    }

    @Test
    void findSubtasksByGoalId_FilterByTitle() {
        long goalId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle("Another");

        Goal mockGoal1 = new Goal();
        mockGoal1.setId(1L);
        mockGoal1.setTitle("Test Goal");
        mockGoal1.setDescription("Description");
        mockGoal1.setStatus(GoalStatus.ACTIVE);

        Goal mockGoal2 = new Goal();
        mockGoal2.setId(2L);
        mockGoal2.setTitle("Another Goal");
        mockGoal2.setDescription("Description");
        mockGoal2.setStatus(GoalStatus.COMPLETED);
        Stream<Goal> stream = Stream.of(mockGoal1, mockGoal2);

        GoalDto goalDto1 = new GoalDto();
        goalDto1.setId(2L);
        goalDto1.setTitle("Another Goal");
        goalDto1.setDescription("Description");
        goalDto1.setStatus("completed");

        List<Goal> resultList = List.of(mockGoal2);
        Stream<Goal> resultStream = resultList.stream();

        when(goalRepository.findByParent(goalId)).thenReturn(stream);
        when(goalFilter.isApplicable(filter)).thenReturn(true);
        when(goalFilter.apply(stream, filter)).thenReturn(resultStream);
        when(goalMapper.toDto(mockGoal2)).thenReturn(goalDto1);

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertEquals(1, result.size());
        assertEquals(goalDto1.getId(), result.get(0).getId());
        assertEquals(goalDto1.getTitle(), result.get(0).getTitle());
        assertEquals(goalDto1.getStatus(), result.get(0).getStatus());
    }

    // test for getGoalsByUser
    @Test
    public void getGoalsByUser_WithFilterByStatus() {
        long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setStatus(GoalStatus.ACTIVE);

        Goal mockGoal1 = new Goal();
        mockGoal1.setId(1L);
        mockGoal1.setTitle("Test Goal");
        mockGoal1.setDescription("Description");
        mockGoal1.setStatus(GoalStatus.ACTIVE);

        Goal mockGoal2 = new Goal();
        mockGoal2.setId(2L);
        mockGoal2.setTitle("Another Goal");
        mockGoal2.setDescription("Description");
        mockGoal2.setStatus(GoalStatus.COMPLETED);
        Stream<Goal> stream = Stream.of(mockGoal1, mockGoal2);

        GoalDto goalDto1 = new GoalDto();
        goalDto1.setId(1L);
        goalDto1.setTitle("Test Goal");
        goalDto1.setDescription("Description");
        goalDto1.setStatus("active");

        List<Goal> resultList = List.of(mockGoal1);
        Stream<Goal> resultStream = resultList.stream();

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(stream);
        when(goalFilter.isApplicable(filter)).thenReturn(true);
        when(goalFilter.apply(stream, filter)).thenReturn(resultStream);
        when(goalMapper.toDto(mockGoal1)).thenReturn(goalDto1);

        List<GoalDto> result = goalService.getGoalsByUser(userId, filter);

        assertEquals(1, result.size());
        assertEquals(goalDto1.getId(), result.get(0).getId());
        assertEquals(goalDto1.getTitle(), result.get(0).getTitle());
        assertEquals(goalDto1.getStatus(), result.get(0).getStatus());

        verify(goalRepository, times(1)).findGoalsByUserId(userId);
    }

    @Test
    public void getGoalsByUser_WithFilterByTitle() {
        long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setTitle("Another");

        Goal mockGoal1 = new Goal();
        mockGoal1.setId(1L);
        mockGoal1.setTitle("Test Goal");
        mockGoal1.setDescription("Description");
        mockGoal1.setStatus(GoalStatus.ACTIVE);

        Goal mockGoal2 = new Goal();
        mockGoal2.setId(2L);
        mockGoal2.setTitle("Another Goal");
        mockGoal2.setDescription("Description");
        mockGoal2.setStatus(GoalStatus.COMPLETED);
        Stream<Goal> stream = Stream.of(mockGoal1, mockGoal2);

        GoalDto goalDto1 = new GoalDto();
        goalDto1.setId(2L);
        goalDto1.setTitle("Another Goal");
        goalDto1.setDescription("Description");
        goalDto1.setStatus("completed");

        List<Goal> resultList = List.of(mockGoal2);
        Stream<Goal> resultStream = resultList.stream();

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(stream);
        when(goalFilter.isApplicable(filter)).thenReturn(true);
        when(goalFilter.apply(stream, filter)).thenReturn(resultStream);
        when(goalMapper.toDto(mockGoal2)).thenReturn(goalDto1);

        List<GoalDto> result = goalService.getGoalsByUser(userId, filter);

        assertEquals(1, result.size());
        assertEquals(goalDto1.getId(), result.get(0).getId());
        assertEquals(goalDto1.getTitle(), result.get(0).getTitle());
        assertEquals(goalDto1.getStatus(), result.get(0).getStatus());

        verify(goalRepository, times(1)).findGoalsByUserId(userId);
    }

}
