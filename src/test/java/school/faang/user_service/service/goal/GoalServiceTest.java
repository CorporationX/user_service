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

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.User;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoalServiceTest {
    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalMapper goalMapper;
    private List<Goal> mockSubtasks;
    private List<GoalDto> mockDtoList;

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

    @Test
    public void testFindSubtasksByGoalId_ValidGoalIdNoFilters_ReturnsAllSubtasks() {
        long goalId = 1;
        GoalFilterDto filter = null;

        when(goalRepository.findByParent(goalId)).thenReturn(mockSubtasks.stream());
        when(filter1.isApplicable(filter)).thenReturn(false);
        when(filter2.isApplicable(filter)).thenReturn(false);
        when(goalMapper.toDto(any())).thenReturn(mockDtoList.get(0), mockDtoList.get(1));

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertEquals(mockDtoList, result);
    }

    @Test
    public void testFindSubtasksByGoalId_ValidGoalIdWithFilters_ReturnsFilteredSubtasks() {
        long goalId = 1;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setSkillIds(List.of(1L));

        when(goalRepository.findByParent(goalId)).thenReturn(mockSubtasks.stream());
        when(filter1.isApplicable(filter)).thenReturn(true);
        when(filter2.isApplicable(filter)).thenReturn(false);
        when(filter1.applyFilter(any(), any())).thenReturn(mockSubtasks.stream());
        when(goalMapper.toDto(any())).thenReturn(mockDtoList.get(0), mockDtoList.get(1));

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertEquals(mockDtoList, result);
    }

    @Test
    public void testFindSubtasksByGoalId_InvalidGoalId() {
        long invalidGoalId = 999;
        GoalFilterDto filter = null;

        when(goalRepository.findByParent(invalidGoalId)).thenReturn(Stream.empty());

        List<GoalDto> result = goalService.findSubtasksByGoalId(invalidGoalId, filter);

        assertEquals(0, result.size());
    }

    @Test
    public void testFindSubtasksByGoalId_ValidGoalIdWithApplicableFilters_ReturnsFilteredSubtasks() {
        long goalId = 1;
        GoalFilterDto filter = new GoalFilterDto();
        filter.setSkillIds(List.of(1L));

        when(goalRepository.findByParent(goalId)).thenReturn(mockSubtasks.stream());
        when(filter1.isApplicable(filter)).thenReturn(true);
        when(filter2.isApplicable(filter)).thenReturn(true);
        when(filter1.applyFilter(any(), any())).thenReturn(mockSubtasks.stream());
        when(filter2.applyFilter(any(), any())).thenReturn(mockSubtasks.stream());
        when(goalMapper.toDto(any())).thenReturn(mockDtoList.get(0), mockDtoList.get(1));

        List<GoalDto> result = goalService.findSubtasksByGoalId(goalId, filter);

        assertEquals(mockDtoList, result);
    }

    @Test
    public void testFindSubtasksByGoalId_NullFilter_ReturnsAllSubtasks() {
        long goalId = 1;
        GoalFilterDto filter = null;

        when(goalRepository.findByParent(goalId)).thenReturn(mockSubtasks.stream());
        when(filter1.isApplicable(filter)).thenReturn(false);
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