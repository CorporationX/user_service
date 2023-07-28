package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.GoalValidator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private List<GoalFilter> goalFilters;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private GoalValidator goalValidator;

    @InjectMocks
    private GoalService goalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetGoalsByUser() {
        Long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();
        List<GoalDto> mockGoals = Collections.singletonList(new GoalDto());

        GoalFilter mockGoalFilter = mock(GoalFilter.class);

        Goal mockGoal = new Goal();
        when(goalRepository.findAll()).thenReturn(List.of(mockGoal));

        when(goalFilters.stream()).thenReturn(Stream.of(mockGoalFilter));
        when(mockGoalFilter.isApplicable(any(GoalFilterDto.class))).thenReturn(true);
        when(mockGoalFilter.applyFilter(any(Stream.class), any(GoalFilterDto.class)))
                .thenReturn(Stream.of(mockGoal));
        when(goalMapper.toDto(any(Goal.class))).thenReturn(mockGoals.get(0));

        List<GoalDto> result = goalService.getGoalsByUser(userId, filters);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockGoals, result);
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
}