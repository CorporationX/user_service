package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GoalServiceTest {

    @Autowired
    private GoalService goalService;
    private GoalRepository goalRepository;
    private GoalMapper goalMapper;
    private GoalValidator goalValidator;
    private SkillRepository skillRepository;
    private GoalFilter goalFilter;
    private List<GoalFilter> goalFilters;

    @BeforeEach
    public void setUp() {
        goalRepository = mock(GoalRepository.class);
        goalMapper = mock(GoalMapper.class);
        goalValidator = mock(GoalValidator.class);
        skillRepository = mock(SkillRepository.class);
        goalFilter = mock(GoalFilter.class);
        goalFilters = List.of(goalFilter);
        goalService = new GoalService(goalRepository, goalMapper, goalValidator, skillRepository, goalFilters);
    }

    @Test
    void create_GoalIsValid_GoalIsCreating() {
        long userId = 1L;
        GoalDto expectedDto = getGoalDto();
        Goal goal = getGoal();
        when(goalRepository.create(expectedDto.getTitle(), expectedDto.getDescription(), expectedDto.getParentId()))
                .thenReturn(goal);
        when(goalRepository.save(goal)).thenReturn(goal);
        when(goalMapper.toDto(goal)).thenReturn(expectedDto);

        GoalDto actualDto = goalService.createGoal(userId, expectedDto);

        verify(goalValidator, times(1)).validateGoalCreation(expectedDto.getId(), expectedDto, 3);
        verify(goalRepository, times(1)).assignGoalToUser(goal.getId(), userId);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void update_GoalIsValid_GoalIsUpdating() {
        long goalId = 1L;
        GoalDto expectedDto = getGoalDto();
        Goal goal = getGoal();
        when(goalRepository.findById(goalId)).thenReturn(Optional.ofNullable(goal));
        when(goalRepository.save(goal)).thenReturn(goal);
        when(goalMapper.toDto(goal)).thenReturn(expectedDto);

        GoalDto actualDto = goalService.updateGoal(goalId, expectedDto);

        verify(goalValidator, times(1)).validateGoalUpdate(goalId, expectedDto);
        verify(goalValidator, times(1)).validateGoalExists(expectedDto.getParentId());
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void delete_GoalIdIsValid_IsDeleting() {
        long goalId = 1L;

        assertDoesNotThrow(() -> goalService.deleteGoal(goalId));
        verify(goalValidator, times(1)).validateGoalExists(goalId);
        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    void findSubtasksByGoalId_GoalIdIsValid_DoesNotThrows() {
        long goalId = 1L;
        GoalFilterDto filters = new GoalFilterDto();
        Stream<Goal> goals = getGoals();
        when(goalRepository.findByParent(goalId)).thenReturn(goals);
        when(goalFilter.isApplicable(filters)).thenReturn(true);

        assertDoesNotThrow(() -> goalService.findSubtasksByGoalId(goalId, filters));
        verify(goalFilter, times(1)).isApplicable(filters);
        verify(goalFilter, times(1)).apply(anyList(), any(GoalFilterDto.class));
        verify(goalValidator, times(1)).validateGoalExists(goalId);
        verify(goalRepository, times(1)).findByParent(goalId);
        verify(goalMapper, times(1)).toDto(anyList());
    }

    @Test
    void getGoalsByUserTest() {
        long userId = 1L;
        GoalFilterDto filters = new GoalFilterDto();
        Stream<Goal> goals = getGoals();
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals);
        when(goalFilter.isApplicable(filters)).thenReturn(true);

        goalService.getGoalsByUser(userId, filters);
        verify(goalFilter, times(1)).isApplicable(filters);
        verify(goalFilter, times(1)).apply(anyList(), any(GoalFilterDto.class));
        verify(goalRepository, times(1)).findGoalsByUserId(userId);
        verify(goalMapper, times(1)).toDto(anyList());
    }

    private GoalDto getGoalDto() {
        return GoalDto.builder()
                .id(1L)
                .parentId(1L)
                .title("Title")
                .status(GoalStatus.ACTIVE)
                .description("Description")
                .skillIds(List.of(1L, 2L, 3L))
                .build();
    }

    private Goal getGoal() {
        return Goal.builder()
                .id(1L)
                .parent(new Goal())
                .status(GoalStatus.ACTIVE)
                .title("Title")
                .description("Description")
                .build();
    }

    private Stream<Goal> getGoals() {
        return Stream.of(
                Goal.builder().id(1L).parent(new Goal()).status(GoalStatus.ACTIVE).title("Title1").description("Description1").build(),
                Goal.builder().id(2L).parent(new Goal()).status(GoalStatus.ACTIVE).title("Title2").description("Description2").build(),
                Goal.builder().id(3L).parent(new Goal()).status(GoalStatus.ACTIVE).title("Title3").description("Description3").build()
        );
    }
}
