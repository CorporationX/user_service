package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GoalTitleFilterTest {

    private static final String TEST = "Test";
    private static final String GOAL_1 = "Goal 1";
    private static final String GOAL_2 = "Goal 2";
    private static final String TEST_GOAL_1 = TEST + " " + GOAL_1;
    private static final String TEST_GOAL_2 = TEST + " " + GOAL_2;

    @InjectMocks
    private GoalTitleFilter goalTitleFilter;

    private GoalFilterDto goalFilterDto;
    private Goal goal1;
    private Goal goal2;

    @BeforeEach
    public void setUp() {
        goal1 = new Goal();
        goal1.setTitle(TEST_GOAL_1);

        goal2 = new Goal();
        goal2.setTitle(TEST_GOAL_2);

        goalFilterDto = new GoalFilterDto();
    }

    @Nested
    @DisplayName("isApplicable Method Tests")
    class IsApplicableTests {

        @Test
        @DisplayName("Returns true when the title is present")
        void whenTitleIsPresentThenReturnTrue() {
            goalFilterDto.setTitle(TEST);

            boolean result = goalTitleFilter.isApplicable(goalFilterDto);

            assertTrue(result, "Filter should be applicable when title is provided");
        }

        @Test
        @DisplayName("Returns false when the title is absent")
        void whenTitleIsAbsentThenReturnFalse() {
            boolean result = goalTitleFilter.isApplicable(goalFilterDto);

            assertFalse(result, "Filter should not be applicable when title is not provided");
        }
    }

    @Nested
    @DisplayName("apply Method Tests")
    class ApplyTests {

        @Test
        @DisplayName("Filters by exact title when the exact title is provided")
        void whenExactTitleProvidedThenFilterByExactTitle() {
            goalFilterDto.setTitle(GOAL_1);

            Stream<Goal> goalsStream = Stream.of(goal1, goal2);
            List<Goal> filteredGoals = goalTitleFilter.apply(goalsStream, goalFilterDto).toList();

            assertEquals(1, filteredGoals.size(), "There should be one goal with 'Goal 1' in the title");
            assertEquals(TEST_GOAL_1, filteredGoals.get(0).getTitle(), "The filtered goal should have 'Test Goal 1' as title");
        }

        @Test
        @DisplayName("Filters by partial title when a partial title is provided")
        void whenPartialTitleProvidedThenFilterByPartialTitle() {
            goalFilterDto.setTitle(TEST);

            Stream<Goal> goalsStream = Stream.of(goal1, goal2);
            List<Goal> filteredGoals = goalTitleFilter.apply(goalsStream, goalFilterDto).toList();

            assertEquals(2, filteredGoals.size(), "There should be two goals with 'Test' in the title");
            assertTrue(filteredGoals.stream().anyMatch(goal -> goal.getTitle().equals(TEST_GOAL_1)));
            assertTrue(filteredGoals.stream().anyMatch(goal -> goal.getTitle().equals(TEST_GOAL_2)));
        }

        @Test
        @DisplayName("Returns an empty list when no match is found")
        void whenNoMatchThenReturnEmptyList() {
            goalFilterDto.setTitle("Unknown");

            Stream<Goal> goalsStream = Stream.of(goal1, goal2);
            List<Goal> filteredGoals = goalTitleFilter.apply(goalsStream, goalFilterDto).toList();

            assertEquals(0, filteredGoals.size(), "There should be no goals with 'Unknown' in the title");
        }
    }
}