package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoalFiltersTest {

    private Goal goal1;
    private Goal goal2;

    @BeforeEach
    void setUp() {
        goal1 = new Goal();
        goal1.setId(1L);
        goal1.setTitle("Learn Java");
        goal1.setStatus(GoalStatus.ACTIVE);
        goal1.setUpdatedAt(LocalDateTime.of(2025, 3, 17, 10, 0));

        goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Learn Spring");
        goal2.setStatus(GoalStatus.COMPLETED);
        goal2.setUpdatedAt(LocalDateTime.of(2025, 3, 17, 12, 0));
    }

    @Test
    void goalStatusFilter_appliesWhenStatusIsSet() {
        GoalStatusFilter filter = new GoalStatusFilter();
        SearchGoalDto searchGoal = new SearchGoalDto("", GoalStatus.ACTIVE, null);
        Stream<Goal> goals = Stream.of(goal1, goal2);

        boolean isApplicable = filter.isApplicable(searchGoal);
        List<Goal> filteredGoals = filter.apply(goals, searchGoal).toList();

        assertTrue(isApplicable);
        assertEquals(1, filteredGoals.size());
        assertEquals(goal1, filteredGoals.get(0));
    }

    @Test
    void goalStatusFilter_doesNotApplyWhenStatusIsNull() {
        GoalStatusFilter filter = new GoalStatusFilter();
        SearchGoalDto searchGoal = new SearchGoalDto("Learn Java", null, null);
        Stream<Goal> goals = Stream.of(goal1, goal2);

        boolean isApplicable = filter.isApplicable(searchGoal);
        List<Goal> filteredGoals = filter.apply(goals, searchGoal).toList();

        assertFalse(isApplicable);
        assertEquals(2, filteredGoals.size());
        assertTrue(filteredGoals.contains(goal1));
        assertTrue(filteredGoals.contains(goal2));
    }

    @Test
    void goalStatusFilter_emptyStream_returnsEmpty() {
        GoalStatusFilter filter = new GoalStatusFilter();
        SearchGoalDto searchGoal = new SearchGoalDto("", GoalStatus.ACTIVE, null);
        Stream<Goal> goals = Stream.empty();

        boolean isApplicable = filter.isApplicable(searchGoal);
        List<Goal> filteredGoals = filter.apply(goals, searchGoal).toList();

        assertTrue(isApplicable);
        assertEquals(0, filteredGoals.size());
    }

    @Test
    void goalTitleFilter_appliesWhenTitleIsSet() {
        GoalTitleFilter filter = new GoalTitleFilter();
        SearchGoalDto searchGoal = new SearchGoalDto("Learn Java", null, null);
        Stream<Goal> goals = Stream.of(goal1, goal2);

        boolean isApplicable = filter.isApplicable(searchGoal);
        List<Goal> filteredGoals = filter.apply(goals, searchGoal).toList();

        assertTrue(isApplicable);
        assertEquals(1, filteredGoals.size());
        assertEquals(goal1, filteredGoals.get(0));
    }

    @Test
    void goalTitleFilter_doesNotApplyWhenTitleIsBlank() {
        GoalTitleFilter filter = new GoalTitleFilter();
        SearchGoalDto searchGoal = new SearchGoalDto("", null, null);
        Stream<Goal> goals = Stream.of(goal1, goal2);

        boolean isApplicable = filter.isApplicable(searchGoal);
        List<Goal> filteredGoals = filter.apply(goals, searchGoal).toList();

        assertFalse(isApplicable);
        assertEquals(2, filteredGoals.size());
        assertTrue(filteredGoals.contains(goal1));
        assertTrue(filteredGoals.contains(goal2));
    }

    @Test
    void goalTitleFilter_emptyStream_returnsEmpty() {
        GoalTitleFilter filter = new GoalTitleFilter();
        SearchGoalDto searchGoal = new SearchGoalDto("Learn Java", null, null);
        Stream<Goal> goals = Stream.empty();

        boolean isApplicable = filter.isApplicable(searchGoal);
        List<Goal> filteredGoals = filter.apply(goals, searchGoal).toList();

        assertTrue(isApplicable);
        assertEquals(0, filteredGoals.size());
    }

    @Test
    void goalTitleFilter_doesNotApplyWhenTitleIsNull() {
        GoalTitleFilter filter = new GoalTitleFilter();
        SearchGoalDto searchGoal = new SearchGoalDto(null, null, null); // title = null
        Stream<Goal> goals = Stream.of(goal1, goal2);

        boolean isApplicable = filter.isApplicable(searchGoal);
        List<Goal> filteredGoals = filter.apply(goals, searchGoal).toList();

        assertFalse(isApplicable);
        assertEquals(2, filteredGoals.size());
        assertTrue(filteredGoals.contains(goal1));
        assertTrue(filteredGoals.contains(goal2));
    }

    @Test
    void goalUpdatedFilter_appliesWhenUpdatedAtIsSet() {
        GoalUpdatedFilter filter = new GoalUpdatedFilter();
        LocalDateTime updatedAt = LocalDateTime.of(2025, 3, 17, 10, 0);
        SearchGoalDto searchGoal = new SearchGoalDto("", null, updatedAt);
        Stream<Goal> goals = Stream.of(goal1, goal2);

        boolean isApplicable = filter.isApplicable(searchGoal);
        List<Goal> filteredGoals = filter.apply(goals, searchGoal).toList();

        assertTrue(isApplicable);
        assertEquals(1, filteredGoals.size());
        assertEquals(goal1, filteredGoals.get(0));
    }

    @Test
    void goalUpdatedFilter_doesNotApplyWhenUpdatedAtIsNull() {
        GoalUpdatedFilter filter = new GoalUpdatedFilter();
        SearchGoalDto searchGoal = new SearchGoalDto("Learn Java", null, null);
        Stream<Goal> goals = Stream.of(goal1, goal2);

        boolean isApplicable = filter.isApplicable(searchGoal);
        List<Goal> filteredGoals = filter.apply(goals, searchGoal).toList();

        assertFalse(isApplicable);
        assertEquals(2, filteredGoals.size());
        assertTrue(filteredGoals.contains(goal1));
        assertTrue(filteredGoals.contains(goal2));
    }

    @Test
    void goalUpdatedFilter_handlesNullUpdatedAtInGoal() {
        GoalUpdatedFilter filter = new GoalUpdatedFilter();
        LocalDateTime updatedAt = LocalDateTime.of(2025, 3, 17, 10, 0);
        SearchGoalDto searchGoal = new SearchGoalDto("", null, updatedAt);

        Goal goalWithNullUpdatedAt = new Goal();
        goalWithNullUpdatedAt.setId(3L);
        goalWithNullUpdatedAt.setTitle("Test");
        goalWithNullUpdatedAt.setStatus(GoalStatus.ACTIVE);

        Stream<Goal> goals = Stream.of(goal1, goalWithNullUpdatedAt);

        boolean isApplicable = filter.isApplicable(searchGoal);
        List<Goal> filteredGoals = filter.apply(goals, searchGoal).toList();

        assertTrue(isApplicable);
        assertEquals(1, filteredGoals.size());
        assertEquals(goal1, filteredGoals.get(0));
    }

    @Test
    void goalUpdatedFilter_emptyStream_returnsEmpty() {
        GoalUpdatedFilter filter = new GoalUpdatedFilter();
        LocalDateTime updatedAt = LocalDateTime.of(2025, 3, 17, 10, 0);
        SearchGoalDto searchGoal = new SearchGoalDto("", null, updatedAt);
        Stream<Goal> goals = Stream.empty();

        boolean isApplicable = filter.isApplicable(searchGoal);
        List<Goal> filteredGoals = filter.apply(goals, searchGoal).toList();

        assertTrue(isApplicable);
        assertEquals(0, filteredGoals.size());
    }
}
