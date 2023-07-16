package school.faang.user_service.service.goal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalMapper goalMapper;
    private GoalFilter filter1;
    private GoalFilter filter2;
    private GoalService goalService;

    @BeforeEach
    public void setUp() {
        filter1 = mock(GoalFilter.class);
        filter2 = mock(GoalFilter.class);

        List<GoalFilter> goalFilters = Arrays.asList(filter1, filter2);
        goalService = new GoalService(goalRepository, goalMapper, goalFilters);
    }

    @Test
    public void testGetGoalsByUser_EmptyGoals() {
        Mockito.when(goalRepository.findAll()).thenReturn(Collections.emptyList());

        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertTrue(result.isEmpty());

        Mockito.verify(goalRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoInteractions(goalMapper);
    }

    @Test
    public void testGetGoalsByUser_NoFilters() {
        List<Goal> goals = new ArrayList<>();
        goals.add(new Goal());
        Mockito.when(goalRepository.findAll()).thenReturn(goals);

        List<GoalDto> goalDtos = new ArrayList<>();
        goalDtos.add(new GoalDto());
        Mockito.when(goalMapper.toDto(any(Goal.class))).thenReturn(goalDtos.get(0));

        Long userId = 1L;
        GoalFilterDto filters = null;

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertEquals(1, result.size());

        Mockito.verify(goalRepository, Mockito.times(1)).findAll();
        Mockito.verify(goalMapper, Mockito.times(1)).toDto(any(Goal.class));
    }

    @Test
    public void testGetGoalsByUser_WithFilters() {
        List<Goal> goals = new ArrayList<>();
        goals.add(new Goal());
        Mockito.when(goalRepository.findAll()).thenReturn(goals);

        List<GoalDto> goalDtos = new ArrayList<>();
        goalDtos.add(new GoalDto());
        Mockito.when(goalMapper.toDto(any(Goal.class))).thenReturn(goalDtos.get(0));

        lenient().when(filter1.isApplicable(any(GoalFilterDto.class))).thenReturn(true);
        lenient().when(filter2.isApplicable(any(GoalFilterDto.class))).thenReturn(false);

        GoalFilterDto filters = new GoalFilterDto();

        Long userId = 1L;

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertEquals(1, result.size());

        Mockito.verify(goalRepository, Mockito.times(1)).findAll();
        Mockito.verify(goalMapper, Mockito.times(1)).toDto(any(Goal.class));

        Mockito.verify(filter1, Mockito.times(1)).isApplicable(any(GoalFilterDto.class));
        Mockito.verify(filter1, Mockito.times(1)).applyFilter(any(Stream.class), any(GoalFilterDto.class));
        Mockito.verify(filter2, Mockito.times(1)).isApplicable(any(GoalFilterDto.class));
        Mockito.verifyNoMoreInteractions(filter2);
    }

    @Test
    public void testGetGoalsByUser_MultipleFilters() {
        List<Goal> goals = new ArrayList<>();
        goals.add(new Goal());
        Mockito.when(goalRepository.findAll()).thenReturn(goals);

        List<GoalDto> goalDtos = new ArrayList<>();
        goalDtos.add(new GoalDto());
        Mockito.when(goalMapper.toDto(any(Goal.class))).thenReturn(goalDtos.get(0));

        lenient().when(filter1.isApplicable(any(GoalFilterDto.class))).thenReturn(true);
        lenient().when(filter2.isApplicable(any(GoalFilterDto.class))).thenReturn(true);

        GoalFilterDto filters = new GoalFilterDto();

        Long userId = 1L;

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertEquals(1, result.size());

        Mockito.verify(goalRepository, Mockito.times(1)).findAll();
        Mockito.verify(goalMapper, Mockito.times(1)).toDto(any(Goal.class));

        Mockito.verify(filter1, Mockito.times(1)).isApplicable(any(GoalFilterDto.class));
        Mockito.verify(filter1, Mockito.times(1)).applyFilter(any(Stream.class), any(GoalFilterDto.class));
        Mockito.verify(filter2, Mockito.times(1)).isApplicable(any(GoalFilterDto.class));
        Mockito.verify(filter2, Mockito.times(1)).applyFilter(any(Stream.class), any(GoalFilterDto.class));
    }
}