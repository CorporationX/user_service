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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest {

    @InjectMocks
    private GoalValidator goalValidator;
    @Mock
    private GoalRepository goalRepository;

    private final Long GOAL_ID_NEGATIVE_ONE = -1L;
    private final Long USER_ID_IS_ONE = 1L;
    private final Long GOAL_ID_IS_ONE = 1L;
    private final Long GOAL_ID_IS_TWO = 2L;

    private final int MAX_LIMIT_GOALS_COUNT = 3;

    @Nested
    class NegativeTests {

        @Nested
        class GoalIdIsPositiveAndNotNullOrElseThrowValidationExceptionMethod {

            @Test
            @DisplayName("Ошибка валидации если переданный id цели = null")
            void whenIdIsNullValueThenThrowValidationException() {
                assertThrows(ValidationException.class,
                        () -> goalValidator.goalIdIsPositiveAndNotNullOrElseThrowValidationException(null),
                        "Goal id can't be null");
            }

            @Test
            @DisplayName("Ошибка валидации если переданный id цели отрицательный")
            void whenIdIsNegativeValueThenThrowValidationException() {
                assertThrows(ValidationException.class,
                        () -> goalValidator.goalIdIsPositiveAndNotNullOrElseThrowValidationException(
                                GOAL_ID_NEGATIVE_ONE),
                        "Goal id can't be less than 0");
            }
        }

        @Nested
        class GoalIsExistedOrElseThrowExceptionMethod {

            @Test
            @DisplayName("Ошибка валидации если цели с переданным id не существует")
            void whenGoalNotExistsThenThrowValidationException() {
                when(goalRepository.findById(anyLong())).thenReturn(Optional.empty());

                assertThrows(ValidationException.class,
                        () -> goalValidator.goalIsExistedOrElseThrowException(GOAL_ID_IS_ONE),
                        "Goal with id " + GOAL_ID_IS_ONE + " not exists");
            }
        }

        @Nested
        class UserActiveGoalsAreLessThenIncomingOrElseThrowExceptionMethod {

            @Test
            @DisplayName("Ошибка валидации если у пользователя активных целей больше или равно лимиту")
            void whenUserActiveGoalsMoreOrEqualsLimitThenThrowValidationException() {
                when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(MAX_LIMIT_GOALS_COUNT);

                assertThrows(ValidationException.class,
                        () -> goalValidator.userActiveGoalsAreLessThenIncomingOrElseThrowException(USER_ID_IS_ONE,
                                MAX_LIMIT_GOALS_COUNT),
                        "User " + USER_ID_IS_ONE + " has max active goals");
            }
        }

        @Nested
        class UserNotWorkingWithGoalOrElseThrowExceptionMethod {

            @Test
            @DisplayName("Ошибка валидации если у пользователя есть переданная цель")
            void whenUserActiveGoalsContainsGoalThenThrowValidationException() {
                Stream<Goal> goalStream = Stream.of(
                        Goal.builder()
                                .id(GOAL_ID_IS_ONE)
                                .build(),
                        Goal.builder()
                                .id(GOAL_ID_IS_TWO)
                                .build());

                when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(goalStream);

                assertThrows(ValidationException.class,
                        () -> goalValidator.userNotWorkingWithGoalOrElseThrowException(USER_ID_IS_ONE, GOAL_ID_IS_TWO),
                        "User with id " + USER_ID_IS_ONE + " already has goal with id " + GOAL_ID_IS_TWO);
            }
        }
    }

    @Nested
    class PositiveTests {

        @Nested
        class GoalIdIsPositiveAndNotNullOrElseThrowValidationExceptionMethod {

            @Test
            @DisplayName("Если переданный id цели не null и больше нуля, то метод ничего не возвращает")
            void whenIdIsNullValueThenSuccess() {
                goalValidator.goalIdIsPositiveAndNotNullOrElseThrowValidationException(GOAL_ID_IS_ONE);
            }
        }

        @Nested
        class GoalIsExistedOrElseThrowExceptionMethod {

            @Test
            @DisplayName("Если цель с переданным id существует, то метод ничего не возвращает")
            void whenGoalExistsThenSuccess() {
                when(goalRepository.findById(anyLong())).thenReturn(Optional.of(new Goal()));

                goalValidator.goalIsExistedOrElseThrowException(GOAL_ID_IS_ONE);

                verify(goalRepository).findById(anyLong());
            }
        }

        @Nested
        class UserActiveGoalsAreLessThenIncomingOrElseThrowExceptionMethod {

            @Test
            @DisplayName("Если у пользователя активных целей больше или равно лимиту, то метод ничего не возвращает")
            void whenUserActiveGoalsLessLimitThenSuccess() {
                when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(MAX_LIMIT_GOALS_COUNT - 1);

                goalValidator.userActiveGoalsAreLessThenIncomingOrElseThrowException(USER_ID_IS_ONE,
                        MAX_LIMIT_GOALS_COUNT);

                verify(goalRepository).countActiveGoalsPerUser(anyLong());
            }
        }

        @Nested
        class UserNotWorkingWithGoalOrElseThrowExceptionMethod {

            @Test
            @DisplayName("Если у пользователя нет переданной цели, то метод ничего не возвращает")
            void whenUserActiveGoalsContainsGoalThenThrowValidationException() {
                Stream<Goal> goalStream = Stream.of(
                        Goal.builder()
                                .id(GOAL_ID_IS_ONE)
                                .build());

                when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(goalStream);

                goalValidator.userNotWorkingWithGoalOrElseThrowException(USER_ID_IS_ONE, GOAL_ID_IS_TWO);
            }
        }
    }
}