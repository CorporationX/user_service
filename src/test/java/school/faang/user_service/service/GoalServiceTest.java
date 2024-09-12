package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.goal.SkillService;
import school.faang.user_service.validator.GoalServiceValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private List<GoalFilter> goalFilters;

    @Mock
    private GoalServiceValidator goalServiceValidator;

    private GoalDto goalDto;
    private Goal goal;
    private long goalId;
    private long userId;

    @BeforeEach
    public void setUp() {
        goalId = 1L;
        userId = 1L;
        goalDto = GoalDto.builder()
                .id(goalId)
                .title("New Goal")
                .description("Description")
                .status(GoalStatus.ACTIVE)
                .build();

        goal = new Goal();
        goal.setId(goalId);
        goal.setTitle("Existing Goal");
        goal.setStatus(GoalStatus.ACTIVE);
    }

    @Test
    @DisplayName("Test Goal Creation")
    @Transactional
    public void testCreateGoal() {
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(0);
        when(goalMapper.toGoal(goalDto)).thenReturn(goal);

        GoalDto createdGoal = goalService.createGoal(userId, goalDto);

        verify(goalRepository, times(1)).create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        verify(skillService, times(1)).create(goal.getSkillsToAchieve(), userId);
        verify(goalServiceValidator, times(1)).validateUserGoalLimit(0);

        assertEquals(goalDto, createdGoal);
    }

    @Test
    @DisplayName("Test Goal Update when Goal Exists")
    @Transactional
    public void testUpdateGoalWhenGoalExists() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalMapper.toGoal(goalDto)).thenReturn(goal);
        when(goalMapper.toGoalDto(goal)).thenReturn(goalDto);

        GoalDto updatedGoal = goalService.updateGoal(goalId, goalDto);

        verify(goalRepository, times(1)).save(goal);
        verify(skillService, times(1)).addSkillToUsers(goalRepository.findUsersByGoalId(goalId), goalId);
        verify(goalServiceValidator, times(1)).validateGoalStatusNotCompleted(goal);
        verify(goalServiceValidator, times(1)).validateSkillsExistByTitle(goal.getSkillsToAchieve());

        assertNotNull(updatedGoal);
        assertEquals(goalDto.getTitle(), updatedGoal.getTitle());
    }

    @Test
    @DisplayName("Test Goal Update when Goal Does Not Exist")
    @Transactional
    public void testUpdateGoalWhenGoalDoesNotExist() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> goalService.updateGoal(goalId, goalDto));
        verify(goalRepository, times(0)).save(any(Goal.class));
    }

    @Test
    @DisplayName("Test Goal Deletion")
    @Transactional
    public void testDeleteGoal() {
        Stream<Goal> goalsStream = Stream.of(goal);
        when(goalRepository.findByParent(goalId)).thenReturn(goalsStream);

        goalService.deleteGoal(goalId);

        verify(goalRepository, times(1)).deleteById(goalId);
        verify(goalServiceValidator, times(1)).validateGoalsExist(goalsStream);
    }

    @Test
    @DisplayName("Test fetching subtasks by Goal ID")
    @Transactional
    public void testGetSubtasksByGoalId() {
        GoalFilterDto filterDto = new GoalFilterDto();
        Stream<Goal> goalsStream = Stream.of(goal);

        when(goalRepository.findByParent(goalId)).thenReturn(goalsStream);
        when(goalFilters.stream()).thenReturn(Stream.of());  // Мокаем отсутствие фильтров

        goalService.getSubtasksByGoalId(goalId, filterDto);

        verify(goalRepository, times(1)).findByParent(goalId);
        verify(goalFilters, times(1)).stream();
    }

    @Test
    @DisplayName("Test fetching Goals by User ID")
    @Transactional
    public void testGetGoalsByUser() {
        GoalFilterDto filterDto = new GoalFilterDto();
        Stream<Goal> goalsStream = Stream.of(goal);

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goalsStream);
        when(goalFilters.stream()).thenReturn(Stream.of());  // Мокаем отсутствие фильтров

        goalService.getGoalsByUser(userId, filterDto);

        verify(goalRepository, times(1)).findGoalsByUserId(userId);
        verify(goalFilters, times(1)).stream();
    }
}
