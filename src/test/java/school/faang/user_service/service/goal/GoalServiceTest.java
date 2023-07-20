package school.faang.user_service.service.goal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
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
    @Mock
    private SkillRepository skillRepository;

    @BeforeEach
    void setUp() {
        filter1 = mock(GoalFilter.class);
        filter2 = mock(GoalFilter.class);
        MockitoAnnotations.initMocks(this);
        goalService = new GoalService(goalRepository, goalMapper, new ArrayList<>(),skillRepository);
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
        goalService = new GoalService(goalRepository, goalMapper, goalFilters,skillRepository);

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
        goalService = new GoalService(goalRepository, goalMapper, goalFilters,skillRepository);

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
    public void testUpdateGoal_ValidGoal() {
        GoalDto goalDto = new GoalDto();
        goalDto.setStatus(GoalStatus.ACTIVE);

        Goal existingGoal = new Goal();
        existingGoal.setStatus(GoalStatus.ACTIVE);

        Goal updatedGoal = new Goal();
        updatedGoal.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.findById(any(Long.class))).thenReturn(Optional.of(existingGoal));
        when(goalMapper.toEntity(goalDto)).thenReturn(updatedGoal);
        when(goalRepository.save(any(Goal.class))).thenReturn(updatedGoal);
        when(goalMapper.toDto(updatedGoal)).thenReturn(goalDto);

        long goalId = 1L;
        GoalDto result = goalService.updateGoal(goalId, goalDto);

        assertEquals(goalDto, result);
    }

    @Test
    public void testUpdateGoal_NonExistentGoal() {
        when(goalRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        long nonExistentGoalId = 10L;
        GoalDto goalDto = new GoalDto();
        assertThrows(IllegalArgumentException.class, () -> goalService.updateGoal(nonExistentGoalId, goalDto));
    }

    @Test
    public void testUpdateGoal_CompletedGoal() {
        GoalDto goalDto = new GoalDto();
        goalDto.setStatus(GoalStatus.COMPLETED);

        Goal existingGoal = new Goal();
        existingGoal.setStatus(GoalStatus.COMPLETED);

        when(goalRepository.findById(any(Long.class))).thenReturn(Optional.of(existingGoal));

        long goalId = 2L;
        assertThrows(IllegalArgumentException.class, () -> goalService.updateGoal(goalId, goalDto));
    }
}