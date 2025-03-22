package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.TestGoalActiveStatusFilter;
import school.faang.user_service.filter.goal.TestGoalTitleFilter;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private GoalMapperImpl goalMapper;

    private final GoalFilter goalStatusFilter = new TestGoalActiveStatusFilter();
    private final GoalFilter goalTitleFilter = new TestGoalTitleFilter();

    private GoalService goalService;

    @BeforeEach
    public void setUp() {
        goalService = new GoalService(goalRepository, skillRepository, goalMapper,
                List.of(goalStatusFilter, goalTitleFilter));
    }

    @Test
    public void testGetGoalsByUserIdNoActiveStatus() {
        Goal goal1 = Goal.builder().id(1L).title("FirstGoal").status(GoalStatus.COMPLETED).build();
        Goal goal2 = Goal.builder().id(2L).title("SecondGoal").status(GoalStatus.COMPLETED).build();
        Goal goal3 = Goal.builder().id(3L).title("ThirdGoal").status(GoalStatus.COMPLETED).build();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal1, goal2, goal3));

        List<GoalDto> result = goalService.getGoalsByUserId(1L, new GoalFilterDto(null, null));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetGoalsByUserIdNoSuitableTitle() {
        Goal goal1 = Goal.builder().id(1L).title(null).status(GoalStatus.COMPLETED).build();
        Goal goal2 = Goal.builder().id(2L).title(null).status(GoalStatus.COMPLETED).build();
        Goal goal3 = Goal.builder().id(3L).title(null).status(GoalStatus.COMPLETED).build();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal1, goal2, goal3));

        List<GoalDto> result = goalService.getGoalsByUserId(1L, new GoalFilterDto(null, null));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetGoalsByUserIdTwoActiveStatus() {
        Goal goal1 = Goal.builder().id(1L).title("FirstGoal").status(GoalStatus.ACTIVE).build();
        Goal goal2 = Goal.builder().id(2L).title("SecondGoal").status(GoalStatus.ACTIVE).build();
        Goal goal3 = Goal.builder().id(3L).title("ThirdGoal").status(GoalStatus.COMPLETED).build();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal1, goal2, goal3));

        List<GoalDto> result = goalService.getGoalsByUserId(1L, new GoalFilterDto(null, null));
        assertEquals(1, result.size());
    }

    @Test
    public void testGetGoalsByUserId() {
        Goal goal1 = Goal.builder().id(1L).title("FirstGoal").status(GoalStatus.ACTIVE).build();
        Goal goal2 = Goal.builder().id(2L).title("SecondGoal").status(GoalStatus.COMPLETED).build();
        Goal goal3 = Goal.builder().id(3L).title("ThirdGoal").status(GoalStatus.ACTIVE).build();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal1, goal2, goal3));

        List<GoalDto> result = goalService.getGoalsByUserId(1L, new GoalFilterDto(null, null));
        assertEquals(1, result.size());
    }
}

