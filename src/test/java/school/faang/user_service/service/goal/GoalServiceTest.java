package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    private GoalService goalService;
    private GoalRepository goalRepository;
    private GoalMapper goalMapper;
    private GoalValidator goalValidator;
    private SkillRepository skillRepository;
    private UserRepository userRepository;
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
        userRepository = mock(UserRepository.class);
        goalService = new GoalService(goalRepository, goalMapper, goalValidator, skillRepository, userRepository, goalFilters);
    }

    @Test
    void createGoal_ValidGoalId() {
        long userId = 1L;
        GoalDto expected = getGoalDto();
        Goal goal = getGoal();
        when(goalMapper.toEntity(any(GoalDto.class))).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(goalMapper.toDto(any(Goal.class))).thenReturn(expected);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(getUser()));
        when(goalValidator.validateOptional(Optional.ofNullable(getUser()), String.format("User with ID %d not found", userId))).thenReturn(new User());

        GoalDto actual = goalService.createGoal(userId, expected);

        verify(goalValidator, times(1)).validateGoalCreation(anyLong(), any(GoalDto.class));
        verify(goalMapper, times(1)).toEntity(any(GoalDto.class));
        verify(goalRepository, times(1)).save(any(Goal.class));
        verify(goalMapper, times(1)).toDto(any(Goal.class));
        verify(userRepository, times(1)).findById(anyLong());
        verify(goalValidator, times(2)).validateOptional(any(Optional.class), anyString());
        assertEquals(expected, actual);
    }

    @Test
    void updateGoal_ValidGoalId() {
        long goalId = 1L;
        GoalDto expected = getGoalDto();
        Goal goal = getGoal();
        when(goalMapper.toEntity(any(GoalDto.class))).thenReturn(goal);
        when(goalRepository.save(goal)).thenReturn(goal);
        when(goalMapper.toDto(goal)).thenReturn(expected);

        GoalDto actual = goalService.updateGoal(goalId, expected);

        verify(goalValidator, times(1)).validateGoalUpdate(goalId, expected);
        assertEquals(expected, actual);
    }

    @Test
    void deleteGoal_ValidGoalId() {
        long goalId = 1L;

        assertDoesNotThrow(() -> goalService.deleteGoal(goalId));
        verify(goalValidator, times(1)).validateNull(anyLong());
        verify(goalRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void findSubtasksByGoalId_ValidGoalId_DoesNotThrowException() {
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
    void getGoalsByUser_ValidUserId() {
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

    private User getUser() {
        return User.builder().build();
    }

    private Stream<Goal> getGoals() {
        return Stream.of(
                Goal.builder().id(1L).parent(new Goal()).status(GoalStatus.ACTIVE).title("Title1").description("Description1").build(),
                Goal.builder().id(2L).parent(new Goal()).status(GoalStatus.ACTIVE).title("Title2").description("Description2").build(),
                Goal.builder().id(3L).parent(new Goal()).status(GoalStatus.ACTIVE).title("Title3").description("Description3").build()
        );
    }
}
