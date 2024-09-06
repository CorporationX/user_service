package school.faang.user_service.validator.goal;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest {

    @InjectMocks
    private GoalValidator goalValidator;
    @Mock
    private GoalRepository goalRepository;

    private final Long LONG_NEGATIVE_VALUE_ONE = -1L;
    private final Long LONG_POSITIVE_VALUE_ONE = 1L;
    private final Long LONG_POSITIVE_VALUE_TWO = 2L;

    private final int INT_MAX_LIMIT_GOALS_COUNT = 3;

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка валидации если переданное число null")
        void When_NullValue_Then_ThrowValidationException() {
            assertThrows(ValidationException.class,
                    () -> goalValidator.goalIdIsPositiveAndNotNullOrElseThrowValidationException(null),
                    "Goal id can't be null");
        }

        @Test
        @DisplayName("Ошибка валидации если переданное число отрицательное")
        void When_NegativeValue_Then_ThrowValidationException() {
            assertThrows(ValidationException.class,
                    () -> goalValidator.goalIdIsPositiveAndNotNullOrElseThrowValidationException(LONG_NEGATIVE_VALUE_ONE),
                    "Goal id can't be less than 0");
        }

        @Test
        @DisplayName("Ошибка валидации если цели с переданным id не существует")
        void When_GoalNotExists_Then_ThrowValidationException() {
            when(goalRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(ValidationException.class,
                    () -> goalValidator.goalIsExistedOrElseThrowException(LONG_POSITIVE_VALUE_ONE),
                    "Goal with id " + LONG_POSITIVE_VALUE_ONE + " not exists");
        }

        @Test
        @DisplayName("Ошибка валидации если у пользователя активных целей больше или равно лимиту")
        void When_UserActiveGoalsMoreOrEquals_Then_ThrowValidationException() {
            when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(INT_MAX_LIMIT_GOALS_COUNT);

            assertThrows(ValidationException.class,
                    () -> goalValidator.userActiveGoalsAreLessThenIncomingOrElseThrowException(LONG_POSITIVE_VALUE_ONE, INT_MAX_LIMIT_GOALS_COUNT),
                    "User " + LONG_POSITIVE_VALUE_ONE + " has max active goals");
        }

        @Test
        @DisplayName("Ошибка валидации если у пользователя есть переданная цель")
        void When_UserActiveGoalNotExistsGoal_Then_ThrowValidationException() {
            Goal goal = new Goal();
            goal.setId(LONG_POSITIVE_VALUE_ONE);

            Goal anotherGoal = new Goal();
            goal.setId(LONG_POSITIVE_VALUE_TWO);

            Stream<Goal> goalStream = Stream.of(goal, anotherGoal);

            when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(goalStream);

            assertThrows(ValidationException.class,
                    () -> goalValidator.userNotWorkingWithGoalOrElseThrowException(LONG_POSITIVE_VALUE_ONE, LONG_POSITIVE_VALUE_TWO),
                    "User with id " + LONG_POSITIVE_VALUE_ONE + " already has goal with id " + LONG_POSITIVE_VALUE_TWO);
        }
    }
}