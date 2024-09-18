package school.faang.user_service.validator.goal;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest {

    private final static int MAX_USER_GOALS_LIMIT = 3;
    private final static long USER_ID = 1L;
    private final static GoalStatus COMPLETED_STATUS = GoalStatus.COMPLETED;
    private final static GoalStatus ACTIVE_STATUS = GoalStatus.ACTIVE;
    private final static Long GOAL_ID_NEGATIVE_ONE = -1L;
    private final static Long USER_ID_IS_ONE = 1L;
    private final static Long GOAL_ID_IS_ONE = 1L;
    private final static Long GOAL_ID_IS_TWO = 2L;
    private final static int MAX_LIMIT_GOALS_COUNT = 3;

    @InjectMocks
    private GoalValidator goalValidator;

    @Mock
    private GoalRepository goalRepository;

    private Goal goal;

    @BeforeEach
    public void setUp() {
        goal = new Goal();
        goal.setStatus(ACTIVE_STATUS);
    }

    @Nested
    class NegativeTests {

        @Nested
        class GoalIdIsPositiveAndNotNullOrElseThrowValidationExceptionMethod {

            @Test
            @DisplayName("Ошибка валидации если переданный id цели = null")
            void whenIdIsNullValueThenThrowValidationException() {
                assertThrows(ValidationException.class,
                        () -> goalValidator.validateGoalIdIsPositiveAndNotNull(null),
                        "Goal id can't be null");
            }

            @Test
            @DisplayName("Ошибка валидации если переданный id цели отрицательный")
            void whenIdIsNegativeValueThenThrowValidationException() {
                assertThrows(ValidationException.class,
                        () -> goalValidator.validateGoalIdIsPositiveAndNotNull(
                                GOAL_ID_NEGATIVE_ONE),
                        "Goal id can't be less than 0");
            }
        }

        @Test
        @DisplayName("Ошибка валидации если цели с переданным id не существует")
        void whenGoalNotExistsThenThrowValidationException() {
            when(goalRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(ValidationException.class,
                    () -> goalValidator.validateGoalWithIdIsExisted(GOAL_ID_IS_ONE),
                    "Goal with id " + GOAL_ID_IS_ONE + " not exists");
        }

        @Test
        @DisplayName("Ошибка валидации если у пользователя активных целей больше или равно лимиту")
        void whenUserActiveGoalsMoreOrEqualsLimitThenThrowValidationException() {
            when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(MAX_LIMIT_GOALS_COUNT);

            assertThrows(ValidationException.class,
                    () -> goalValidator.validateUserActiveGoalsAreLessThenIncoming(USER_ID_IS_ONE,
                            MAX_LIMIT_GOALS_COUNT),
                    "User " + USER_ID_IS_ONE + " has max active goals");
        }

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
                    () -> goalValidator.validateUserNotWorkingWithGoal(USER_ID_IS_ONE, GOAL_ID_IS_TWO),
                    "User with id " + USER_ID_IS_ONE + " already has goal with id " + GOAL_ID_IS_TWO);
        }

        @Test
        @DisplayName("Does not throw exception when user does not exceed goal limit")
        void whenUserDoesNotExceedGoalLimitThenDoNotThrowException() {
            when(goalRepository.countActiveGoalsPerUser(USER_ID)).thenReturn(MAX_USER_GOALS_LIMIT - 1);

            goalValidator.validateUserGoalLimit(USER_ID);

            verify(goalRepository).countActiveGoalsPerUser(USER_ID);
        }

        @Test
        @DisplayName("Does not throw exception when goal is not completed")
        void whenGoalIsNotCompletedThenDoNotThrowException() {
            goal.setStatus(ACTIVE_STATUS);

            goalValidator.validateGoalStatusNotCompleted(goal);
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если переданный id цели не null и больше нуля, то метод ничего не возвращает")
        void whenIdIsNullValueThenSuccess() {
            goalValidator.validateGoalIdIsPositiveAndNotNull(GOAL_ID_IS_ONE);
        }

        @Test
        @DisplayName("Если цель с переданным id существует, то метод ничего не возвращает")
        void whenGoalExistsThenSuccess() {
            when(goalRepository.findById(anyLong())).thenReturn(Optional.of(new Goal()));

            goalValidator.validateGoalWithIdIsExisted(GOAL_ID_IS_ONE);

            verify(goalRepository).findById(anyLong());
        }

        @Test
        @DisplayName("Если у пользователя активных целей больше или равно лимиту, то метод ничего не возвращает")
        void whenUserActiveGoalsLessLimitThenSuccess() {
            when(goalRepository.countActiveGoalsPerUser(anyLong())).thenReturn(MAX_LIMIT_GOALS_COUNT - 1);

            goalValidator.validateUserActiveGoalsAreLessThenIncoming(USER_ID_IS_ONE,
                    MAX_LIMIT_GOALS_COUNT);

            verify(goalRepository).countActiveGoalsPerUser(anyLong());
        }

        @Test
        @DisplayName("Если у пользователя нет переданной цели, то метод ничего не возвращает")
        void whenUserActiveGoalsContainsGoalThenThrowValidationException() {
            Stream<Goal> goalStream = Stream.of(
                    Goal.builder()
                            .id(GOAL_ID_IS_ONE)
                            .build());

            when(goalRepository.findGoalsByUserId(anyLong())).thenReturn(goalStream);

            goalValidator.validateUserNotWorkingWithGoal(USER_ID_IS_ONE, GOAL_ID_IS_TWO);
        }

        @Test
        @DisplayName("Throws exception when user exceeds goal limit")
        void whenUserExceedsGoalLimitThenThrowException() {
            // Mock the repository to return a count exceeding the limit
            when(goalRepository.countActiveGoalsPerUser(USER_ID)).thenReturn(MAX_USER_GOALS_LIMIT + 1);

            assertThrows(DataValidationException.class, () ->
                    goalValidator.validateUserGoalLimit(USER_ID), "This user has exceeded the goal limit"
            );

            verify(goalRepository).countActiveGoalsPerUser(USER_ID);
        }

        @Test
        @DisplayName("Throws exception when goal is completed")
        void whenGoalIsCompletedThenThrowException() {
            goal.setStatus(COMPLETED_STATUS);

            assertThrows(DataValidationException.class, () ->
                            goalValidator.validateGoalStatusNotCompleted(goal),
                    "The goal cannot be updated because it is already completed"
            );
        }
    }
}