package school.faang.user_service.filter.goal;


import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;
import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

class StatusGoalFilterTest {
    private StatusGoalFilter statusGoalFilter = new StatusGoalFilter();

    @Test
    public void isApplication_StatusIsApplicableTrue() {
        boolean result = statusGoalFilter.isApplication(new GoalFilterDto(null,
                null,
                ACTIVE,
                null,
                null
        ));
        assertTrue(result);
    }

    @Test
    public void isApplication_StatusIsApplicableFalse() {
        boolean result = statusGoalFilter.isApplication(new GoalFilterDto(null,
                null,
                null,
                null,
                null
        ));
        assertFalse(result);
    }

    @Test
    public void apply_testFilter() {
        Stream<Goal> goalStream = Stream.of(
                Goal.builder().status(ACTIVE).build(),
                Goal.builder().status(ACTIVE).build(),
                Goal.builder().status(COMPLETED).build()
        );
        GoalStatus goalStatus = ACTIVE;
        GoalFilterDto goalFilterDto = new GoalFilterDto(null,
                null,
                goalStatus,
                null,
                null
        );
        Stream<Goal> goalResult = statusGoalFilter.apply(goalStream,
                goalFilterDto);
        List<Goal> goalList = goalResult.toList();

        assertEquals(2, goalList.size());
        assertEquals(goalStatus, goalList.get(0).getStatus());
    }
}