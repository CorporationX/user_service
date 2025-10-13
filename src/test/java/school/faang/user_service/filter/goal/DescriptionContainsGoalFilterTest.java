package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DescriptionContainsGoalFilterTest {
    private final DescriptionContainsGoalFilter descriptionFilter = new DescriptionContainsGoalFilter();

    @Test
    public void isApplication_DescriptionIsApplicableTrue() {
        boolean result = descriptionFilter.isApplication(new GoalFilterDto(null,
                "test",
                null,
                null,
                null
        ));
        assertTrue(result);
    }

    @Test
    public void isApplication_DescriptionIsApplicableFalseNull() {
        boolean result = descriptionFilter.isApplication(new GoalFilterDto(null,
                null,
                null,
                null,
                null
        ));
        assertFalse(result);
    }

    @Test
    public void isApplication_DescriptionIsApplicableFalseEmpty() {
        boolean result = descriptionFilter.isApplication(new GoalFilterDto(null,
                "   ",
                null,
                null,
                null
        ));
        assertFalse(result);
    }

    @Test
    public void apply_TestFilter() {
        Stream<Goal> goalStream = Stream.of(
                Goal.builder().description("OneTest").build(),
                Goal.builder().description("ONETEst").build(),
                Goal.builder().description("FreeTest").build()
        );
        String description = "OnETest";
        GoalFilterDto goalFilterDto = new GoalFilterDto(null,
                description,
                null,
                null,
                null
        );
        Stream<Goal> goalResult = descriptionFilter.apply(goalStream,
                goalFilterDto);
        List<Goal> goalList = goalResult.toList();

        assertEquals(2, goalList.size());
        assertEquals(description.toLowerCase(), goalList.get(0).getDescription().toLowerCase());
    }
}