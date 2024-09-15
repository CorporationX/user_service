package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validator.GoalServiceValidator;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GoalServiceValidatorTest {

    private static final int GOAL_LIMIT = 3;
    private static final int ACTIVE_GOAL_COUNT_BELOW_LIMIT = 2;
    private static final GoalStatus COMPLETED_STATUS = GoalStatus.COMPLETED;
    private static final GoalStatus ACTIVE_STATUS = GoalStatus.ACTIVE;

    @InjectMocks
    private GoalServiceValidator goalServiceValidator;

    private Goal goal;

    @BeforeEach
    public void setUp() {
        goal = new Goal();
        goal.setStatus(ACTIVE_STATUS);
    }

    @Nested
    @DisplayName("User Goal Limit Validation Tests")
    class UserGoalLimitTests {

        @Test
        @DisplayName("Throws exception when user exceeds goal limit")
        void whenUserExceedsGoalLimitThenThrowException() {
            assertThrows(DataValidationException.class, () ->
                            goalServiceValidator.validateUserGoalLimit(GOAL_LIMIT),
                    "This user has exceeded the goal limit"
            );
        }

        @Test
        @DisplayName("Does not throw exception when user does not exceed goal limit")
        void whenUserDoesNotExceedGoalLimitThenDoNotThrowException() {
            goalServiceValidator.validateUserGoalLimit(ACTIVE_GOAL_COUNT_BELOW_LIMIT);
        }
    }

    @Nested
    @DisplayName("Goal Existence Validation Tests")
    class GoalExistenceTests {

        @Test
        @DisplayName("Throws exception when no goals exist")
        void whenNoGoalsExistThenThrowException() {
            Stream<Goal> emptyGoalsStream = Stream.empty();

            assertThrows(DataValidationException.class,
                    () -> goalServiceValidator.validateGoalsExist(emptyGoalsStream),
                    "A goal with this ID does not exist"
            );
        }

        @Test
        @DisplayName("Does not throw exception when goals exist")
        void whenGoalsExistThenDoNotThrowException() {
            Stream<Goal> goalsStream = Stream.of(goal);

            goalServiceValidator.validateGoalsExist(goalsStream);
        }
    }

    @Nested
    @DisplayName("Goal Status Validation Tests")
    class GoalStatusTests {

        @Test
        @DisplayName("Throws exception when goal is completed")
        void whenGoalIsCompletedThenThrowException() {
            goal.setStatus(COMPLETED_STATUS);

            assertThrows(DataValidationException.class,
                    () -> goalServiceValidator.validateGoalStatusNotCompleted(goal),
                    "The goal cannot be updated because it is already completed"
            );
        }

        @Test
        @DisplayName("Does not throw exception when goal is not completed")
        void whenGoalIsNotCompletedThenDoNotThrowException() {
            goal.setStatus(ACTIVE_STATUS);

            goalServiceValidator.validateGoalStatusNotCompleted(goal);
        }
    }
}

