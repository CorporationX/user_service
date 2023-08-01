package school.faang.user_service.service.goal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filters.TitleGoalFilter;
import school.faang.user_service.validation.GoalValidator;

import java.util.Collections;

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

    @BeforeEach
    public void setUp() {
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
        goalService = new GoalService(goalRepository, goalMapper, goalFilters,goalValidator);
        MockitoAnnotations.openMocks(this);
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
        List<Goal> goals = List.of(new Goal(), new Goal());
        when(goalRepository.findAll()).thenReturn(goals);
        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertEquals(0, result.size());
    }

    @Test
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
    void testGetGoalsByUser_WithApplicableGoalFilter() {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        GoalFilter applicableFilter = mock(GoalFilter.class);
        when(applicableFilter.isApplicable(filters)).thenReturn(true);

        List<GoalFilter> goalFilters = Collections.singletonList(applicableFilter);
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
}