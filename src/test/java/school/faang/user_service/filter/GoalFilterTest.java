package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalFilterTest {
    private final GoalMapper goalMapper = GoalMapper.INSTANCE;

    Goal goal = mock(Goal.class);
    Stream<Goal> goals = Stream.of(goal);

    @Test
    void filterGoals_With_Status_As_Exist() {
       GoalFilterDto goalFilterDto = new GoalFilterDto();
       goalFilterDto.setGoalStatus(GoalStatus.ACTIVE);
       GoalFilter goalFilter = new GoalFilter(goalFilterDto, goalMapper);

       when(goal

        List<GoalDto> result = goalFilter.filterGoals(goals);

        assertEquals(0, result.size());
    }
}
