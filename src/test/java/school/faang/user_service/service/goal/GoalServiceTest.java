package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filters.StatusGoalFilter;
import school.faang.user_service.service.goal.filters.TitleGoalFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalMapper goalMapper;

    private GoalService goalService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        goalService = new GoalService(goalRepository, goalMapper);
    }

    @Test
    public void testGetGoalsByUser_EmptyGoals() {
        when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(Stream.empty());
        when(goalMapper.toDto(any())).thenReturn(new GoalDto());

        List<GoalDto> result = goalService.getGoalsByUser(1L, new GoalFilters());

        Assertions.assertEquals(0, result.size());
        verify(goalRepository, times(1)).findGoalsByUserId(anyLong());
        verify(goalMapper, times(0)).toDto(any());
    }

    @Test
    public void testGetGoalsByUser_NoFilters() {
        List<Goal> goals = Arrays.asList(new Goal(), new Goal(), new Goal());
        when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(goals.stream());
        when(goalMapper.toDto(any())).thenReturn(new GoalDto());

        List<GoalDto> result = goalService.getGoalsByUser(1L, new GoalFilters());

        Assertions.assertEquals(goals.size(), result.size());
        verify(goalRepository, times(1)).findGoalsByUserId(anyLong());
        verify(goalMapper, times(goals.size())).toDto(any());
    }

    @Test
    public void testGetGoalsByUser_WithFilters() {
        Goal goal1 = new Goal();
        goal1.setTitle("Goal 1");
        Goal goal2 = new Goal();
        goal2.setTitle("Goal 2");
        List<Goal> goals = Arrays.asList(goal1, goal2);
        when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(goals.stream());
        when(goalMapper.toDto(any())).thenAnswer(invocation -> {
            Goal goal = invocation.getArgument(0);
            GoalDto goalDto = new GoalDto();
            goalDto.setTitle(goal.getTitle());
            return goalDto;
        });

        GoalFilters filters = new GoalFilters();
        filters.addGoalFilter(new TitleGoalFilter("Goal 1"));

        List<GoalDto> result = goalService.getGoalsByUser(1L, filters);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Goal 1", result.get(0).getTitle());
        verify(goalRepository, times(1)).findGoalsByUserId(anyLong());
        verify(goalMapper, times(goals.size())).toDto(any());
    }

    @Test
    public void testGetGoalsByUser_MultipleFilters() {
        Goal goal1 = new Goal();
        goal1.setTitle("Goal 1");
        goal1.setStatus(GoalStatus.COMPLETED);
        Goal goal2 = new Goal();
        goal2.setTitle("Goal 2");
        goal2.setStatus(GoalStatus.ACTIVE);
        List<Goal> goals = Arrays.asList(goal1, goal2);
        when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(goals.stream());
        when(goalMapper.toDto(any())).thenAnswer(invocation -> {
            Goal goal = invocation.getArgument(0);
            GoalDto goalDto = new GoalDto();
            goalDto.setTitle(goal.getTitle());
            goalDto.setStatus(goal.getStatus());
            return goalDto;
        });

        GoalFilters filters = new GoalFilters();
        filters.addGoalFilter(new TitleGoalFilter("Goal 1"));
        filters.addGoalFilter(new StatusGoalFilter(GoalStatus.COMPLETED));

        List<GoalDto> result = goalService.getGoalsByUser(1L, filters);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Goal 1", result.get(0).getTitle());
        Assertions.assertEquals(result.get(0).getStatus(), GoalStatus.COMPLETED);
        verify(goalRepository, times(1)).findGoalsByUserId(anyLong());
        verify(goalMapper, times(goals.size())).toDto(any());
    }
}