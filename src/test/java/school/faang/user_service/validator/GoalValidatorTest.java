package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.NotFoundException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest {

    @Mock
    private User user;
    @Mock
    private Goal goal;
    @InjectMocks
    private GoalValidator validator;
    private final int MAX_GOALS_AMOUNT = 3;

    @Test
    void shouldThrowTooManyGoalsException() {
        when(user.getGoals()).thenReturn(Collections.nCopies(MAX_GOALS_AMOUNT, new Goal()));
        assertThrows(DataValidationException.class, () -> validator.validateGoalCreation(user, goal));
    }

    @Test
    public void testValidateGoalUpdate_ThrowsExceptionIfGoalCompleted() {
        GoalStatus goalStatus = GoalStatus.COMPLETED;
        when(goal.getStatus()).thenReturn(goalStatus);

        DataValidationException thrown = assertThrows(
                DataValidationException.class,
                () -> validator.validateGoalUpdate(goal)
        );

        verify(goal, times(1)).getStatus();
    }

    @Test
    void testValidateThatIdIsGreaterThan0_withInvalidId_throwsDataValidationException() {
        GoalValidator validator = new GoalValidator(null);
        assertThrows(DataValidationException.class, () -> validator.validateThatIdIsGreaterThan0(0));
    }

    @Test
    void testValidateThatIdIsGreaterThan0_withValidId_doesNotThrowException() {
        GoalValidator validator = new GoalValidator(null);
        assertDoesNotThrow(() -> validator.validateThatIdIsGreaterThan0(1));
    }

    @Test
    void testValidateFindSubtasks_withNoSubtasks_throwsNotFoundException() {
        List<Goal> subtasks = Collections.emptyList();
        long goalId = 1L;
        GoalValidator validator = new GoalValidator(null);
        assertThrows(NotFoundException.class, () -> validator.validateFindSubtasks(subtasks, goalId));
    }
}