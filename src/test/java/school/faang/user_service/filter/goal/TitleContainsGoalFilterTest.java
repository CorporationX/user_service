package school.faang.user_service.filter.goal;


import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TitleContainsGoalFilterTest {
    private final TitleContainsGoalFilter titleFilter = new TitleContainsGoalFilter();

    @Test
    public void isApplication_TitleIsApplicableTrue() {
        boolean result = titleFilter.isApplication(new GoalFilterDto("test",
                null,
                null,
                null,
                null
        ));
        assertTrue(result);
    }

    @Test
    public void isApplication_TitleIsApplicableFalseNull() {
        boolean result = titleFilter.isApplication(new GoalFilterDto("",
                null,
                null,
                null,
                null
        ));
        assertFalse(result);
    }

    @Test
    public void isApplication_TitleIsApplicableFalseEmpty() {
        boolean result = titleFilter.isApplication(new GoalFilterDto("    ",
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
                Goal.builder().title("OneTest").build(),
                Goal.builder().title("ONETEst").build(),
                Goal.builder().title("FreeTest").build()
        );
        String title = "OnETest";
        GoalFilterDto goalFilterDto = new GoalFilterDto(title,
                null,
                null,
                null,
                null
        );
        Stream<Goal> goalResult = titleFilter.apply(goalStream,
                goalFilterDto);
        List<Goal> goalList = goalResult.toList();

        assertEquals(2, goalList.size());
        assertEquals(title.toLowerCase(), goalList.get(0).getTitle().toLowerCase());
    }
}