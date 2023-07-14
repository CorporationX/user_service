package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalFilterTest {
    private final GoalMapper goalMapper = GoalMapper.INSTANCE;
    GoalFilterDto goalFilterDto = new GoalFilterDto();
    Goal goal = mock(Goal.class);
    Stream<Goal> goals = Stream.of(goal);

    @Test
    void filterGoals_Successful() {
        GoalFilter goalFilter = new GoalFilter(goalFilterDto, goalMapper);

        List<GoalDto> result = goalFilter.filterGoals(goals);

        assertEquals(1, result.size());
    }

    @Test
    void filterGoals_Status_Doesnt_Match() {
        goalFilterDto.setGoalStatus(GoalStatus.ACTIVE);
        GoalFilter goalFilter = new GoalFilter(goalFilterDto, goalMapper);
        when(goal.getStatus()).thenReturn(GoalStatus.COMPLETED);

        List<GoalDto> result = goalFilter.filterGoals(goals);

        assertEquals(0, result.size());
    }

    @Test
    void filterGoals_Goal_Doesnt_Match() {
        goalFilterDto.setSkillId(1L);
        GoalFilter goalFilter = new GoalFilter(goalFilterDto, goalMapper);

        when(goal.getSkillsToAchieve()).thenReturn(List.of(Skill.builder().id(2L).build()));

        List<GoalDto> result = goalFilter.filterGoals(goals);

        assertEquals(0, result.size());
    }

    @Test
    void filterGoals_Title_Doesnt_Match(){
        goalFilterDto.setTitle("title");
        GoalFilter goalFilter = new GoalFilter(goalFilterDto, goalMapper);

        when(goal.getTitle()).thenReturn("title1");

        List<GoalDto> result = goalFilter.filterGoals(goals);

        assertEquals(0, result.size());
    }
}
