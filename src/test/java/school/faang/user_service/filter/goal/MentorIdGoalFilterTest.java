package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MentorIdGoalFilterTest {
    private MentorIdGoalFilter mentorIdGoalFilter = new MentorIdGoalFilter();

    @Test
    public void isApplication_MentorIsApplicableTrue() {
        boolean result = mentorIdGoalFilter.isApplication(new GoalFilterDto(null,
                null,
                null,
                1L,
                null
        ));
        assertTrue(result);
    }

    @Test
    public void isApplication_MentorIsApplicableFalse() {
        boolean result = mentorIdGoalFilter.isApplication(new GoalFilterDto(null,
                null,
                null,
                null,
                null
        ));
        assertFalse(result);
    }

    @Test
    public void apply_testFilter() {
        User mentor1 = User.builder().id(1L).build();
        User mentor2 = User.builder().id(2L).build();
        Stream<Goal> goalStream = Stream.of(
                Goal.builder().mentor(mentor1).build(),
                Goal.builder().mentor(mentor2).build()
        );
        Long id = 1L;
        GoalFilterDto goalFilterDto = new GoalFilterDto(null,
                null,
                null,
                id,
                null
        );
        Stream<Goal> goalResult = mentorIdGoalFilter.apply(goalStream,
                goalFilterDto);
        List<Goal> goalList = goalResult.toList();

        assertEquals(1, goalList.size());
        User mentor = goalList.get(0).getMentor();
        assertEquals(id, mentor.getId());
    }
}