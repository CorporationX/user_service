package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.filters.ParentIdGoalFilter;
import school.faang.user_service.service.goal.filters.SkillIdsGoalFilter;
import school.faang.user_service.service.goal.filters.StatusGoalFilter;
import school.faang.user_service.service.goal.filters.TitleGoalFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GoalFiltersTest {
    private List<Goal> goals;
    private GoalFilterDto filters;

    @BeforeEach
    public void setUp() {
        goals = createSampleGoals();
        filters = new GoalFilterDto();
    }

    @Test
    public void testParentIdGoalFilter() {
        filters.setParentId(1L);
        List<Goal> expectedGoals = goals.stream()
                .filter(goal -> goal.getParent() != null && goal.getParent().getId().equals(filters.getParentId()))
                .collect(Collectors.toList());

        ParentIdGoalFilter filter = new ParentIdGoalFilter();
        Stream<Goal> filteredGoals = filter.applyFilter(goals.stream(), filters);
        List<Goal> resultGoals = filteredGoals.collect(Collectors.toList());

        Assertions.assertEquals(expectedGoals, resultGoals);
    }

    @Test
    public void testSkillIdsGoalFilter() {
        filters.setSkillIds(List.of(1L, 2L));
        List<Goal> expectedGoals = goals.stream()
                .filter(goal -> new HashSet<>(filters.getSkillIds())
                        .containsAll(goal.getSkillsToAchieve().stream().map(Skill::getId).toList()))
                .collect(Collectors.toList());

        SkillIdsGoalFilter filter = new SkillIdsGoalFilter();
        Stream<Goal> filteredGoals = filter.applyFilter(goals.stream(), filters);
        List<Goal> resultGoals = filteredGoals.collect(Collectors.toList());

        Assertions.assertEquals(expectedGoals, resultGoals);
    }

    @Test
    public void testStatusGoalFilter() {
        filters.setStatus(GoalStatus.ACTIVE);
        List<Goal> expectedGoals = goals.stream()
                .filter(goal -> goal.getStatus() == filters.getStatus())
                .collect(Collectors.toList());

        StatusGoalFilter filter = new StatusGoalFilter();
        Stream<Goal> filteredGoals = filter.applyFilter(goals.stream(), filters);
        List<Goal> resultGoals = filteredGoals.collect(Collectors.toList());

        Assertions.assertEquals(expectedGoals, resultGoals);
    }

    @Test
    public void testTitleGoalFilter() {
        filters.setTitlePattern("abc");
        List<Goal> expectedGoals = goals.stream()
                .filter(goal -> goal.getTitle().contains(filters.getTitlePattern()))
                .collect(Collectors.toList());

        TitleGoalFilter filter = new TitleGoalFilter();
        Stream<Goal> filteredGoals = filter.applyFilter(goals.stream(), filters);
        List<Goal> resultGoals = filteredGoals.collect(Collectors.toList());

        Assertions.assertEquals(expectedGoals, resultGoals);
    }

    private List<Goal> createSampleGoals() {
        List<Goal> goals = new ArrayList<>();

        Goal goal1 = new Goal();
        goal1.setId(1L);
        goal1.setTitle("Goal 1");
        goal1.setParent(null);
        goal1.setSkillsToAchieve(new ArrayList<>());
        goal1.setStatus(GoalStatus.ACTIVE);

        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Goal 2");
        goal2.setParent(goal1);
        goal2.setSkillsToAchieve(new ArrayList<>());
        goal2.setStatus(GoalStatus.COMPLETED);

        Goal goal3 = new Goal();
        goal3.setId(3L);
        goal3.setTitle("Goal 3");
        goal3.setParent(goal1);
        goal3.setSkillsToAchieve(new ArrayList<>());
        goal3.setStatus(GoalStatus.ACTIVE);

        Goal goal4 = new Goal();
        goal4.setId(4L);
        goal4.setTitle("Goal 4");
        goal4.setParent(goal2);
        goal4.setSkillsToAchieve(new ArrayList<>());
        goal4.setStatus(GoalStatus.COMPLETED);

        goals.add(goal1);
        goals.add(goal2);
        goals.add(goal3);
        goals.add(goal4);

        return goals;
    }
}
