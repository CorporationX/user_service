package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private GoalValidator goalValidator;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private List<GoalFilter> goalFilters;
    @InjectMocks
    private GoalService goalService;

    @Test
    public void create_GoalIsValid_GoalIsCreating() {
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
    public void update_GoalIsValid_GoalIsUpdating() {
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
    public void delete_GoalIdIsValid_IsDeleting() {
        long goalId = 1L;

        assertDoesNotThrow(() -> goalService.deleteGoal(goalId));
        verify(goalValidator, times(1)).validateGoalExists(goalId);
        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    public void findSubtasksByGoalId_GoalIdIsValid_DoesNotThrows() {
        long goalId = 1L;
        GoalFilterDto filters = new GoalFilterDto();

        assertDoesNotThrow(() -> goalService.findSubtasksByGoalId(goalId, filters));
        verify(goalValidator, times(1)).validateGoalExists(goalId);
        verify(goalRepository, times(1)).findByParent(goalId);
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

    private List<Goal> getGoals() {
        Goal goal1 = Goal.builder()
                .id(1L)
                .build();
        Goal goal2 = Goal.builder()
                .id(1L)
                .build();
        return List.of(goal1, goal2);
    }

    private List<GoalDto> getGoalDtos() {
        GoalDto goal1 = GoalDto.builder()
                .id(1L)
                .build();
        GoalDto goal2 = GoalDto.builder()
                .id(1L)
                .build();
        return List.of(goal1, goal2);
    }
}
