package school.faang.user_service.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.GoalSkillFilter;
import school.faang.user_service.filter.goal.GoalStatusFilter;
import school.faang.user_service.filter.goal.GoalTitleFilter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GoalFilterTest {

    @Test
    void filterGoals_Successful() {
        GoalFilterDto goalFilterDto = new GoalFilterDto();
        List<Goal> goals = new ArrayList<>(List.of(new Goal()));
        List<GoalFilter> goalFilters = List.of(new GoalTitleFilter(),
                new GoalSkillFilter(), new GoalStatusFilter());

        goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(goalFilterDto))
                .forEach(goalFilter -> goalFilter.apply(goals, goalFilterDto));

        assertEquals(1, goals.size());
    }

    @Test
    void filterGoals_Status_Doesnt_Match() {
        GoalFilterDto goalFilterDto = GoalFilterDto.builder().goalStatus(GoalStatus.ACTIVE).build();
        Goal goal = Goal.builder().status(GoalStatus.COMPLETED).build();
        List<Goal> goals = new ArrayList<>(List.of(goal));
        GoalStatusFilter goalTitleFilter = new GoalStatusFilter();

        goalTitleFilter.apply(goals, goalFilterDto);

        assertEquals(0, goals.size());
    }

    @Test
    void filterGoals_Goal_Doesnt_Match() {
        GoalFilterDto goalFilterDto = GoalFilterDto.builder().skillId(1L).build();
        Skill skill = Skill.builder().id(2L).build();
        Goal goal = Goal.builder().skillsToAchieve(List.of(skill)).build();
        List<Goal> goals = new ArrayList<>(List.of(goal));
        GoalSkillFilter goalTitleFilter = new GoalSkillFilter();

        goalTitleFilter.apply(goals, goalFilterDto);

        assertEquals(0, goals.size());
    }

    @Test
    void filterGoals_Title_Doesnt_Match() {
        GoalFilterDto goalFilterDto = GoalFilterDto.builder().title("tttitle").build();
        Goal goal = Goal.builder().title("Title").build();
        List<Goal> goals = new ArrayList<>(List.of(goal));
        GoalTitleFilter goalTitleFilter = new GoalTitleFilter();

        goalTitleFilter.apply(goals, goalFilterDto);

        assertEquals(0, goals.size());
    }
}
